package com.active4j.hr.oa.leave.mcp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/mcp")
@Slf4j
public class McpController {

    @Autowired
    private LeaveMcpService leaveMcpService;

    private final ConcurrentHashMap<String, HttpServletResponse> sseClients = new ConcurrentHashMap<String, HttpServletResponse>();

    private static final String MCP_VERSION = "2024-11-05";
    private static final String SERVER_NAME = "active4j-leave-mcp-server";
    private static final String SERVER_VERSION = "1.0.0";

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public DeferredResult<Void> sse(HttpServletResponse response) {
        String sessionId = java.util.UUID.randomUUID().toString();
        final DeferredResult<Void> deferredResult = new DeferredResult<Void>(3600_000L);

        try {
            response.setContentType("text/event-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Access-Control-Allow-Origin", "*");

            String endpointUrl = "http://localhost:9002/jsp/mcp/message?id=" + sessionId;
            log.info("SSE 连接已建立, sessionId={}, endpoint={}", sessionId, endpointUrl);

            java.io.PrintWriter writer = response.getWriter();
            writer.write("event: endpoint\n");
            writer.write("data: " + endpointUrl + "\n");
            writer.write("\n");
            writer.flush();

            sseClients.put(sessionId, response);

            deferredResult.onCompletion(new Runnable() {
                @Override
                public void run() {
                    sseClients.remove(sessionId);
                    log.info("SSE 连接完成, sessionId={}", sessionId);
                }
            });

            deferredResult.onTimeout(new Runnable() {
                @Override
                public void run() {
                    sseClients.remove(sessionId);
                    log.info("SSE 连接超时, sessionId={}", sessionId);
                }
            });
        } catch (IOException e) {
            log.error("SSE 连接写入失败", e);
            sseClients.remove(sessionId);
            deferredResult.setResult(null);
        }

        return deferredResult;
    }

    @RequestMapping(value = "/message", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces = "application/json;charset=UTF-8")
    public void message(@RequestBody(required = false) String body, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equals(httpRequest.getMethod())) {
            response.setStatus(200);
            response.getWriter().write("");
            response.getWriter().flush();
            return;
        }

        JSONObject request = JSON.parseObject(body);
        String method = request.getString("method");
        Object id = request.get("id");

        log.info("MCP 请求: method={}, id={}", method, id);

        JSONObject result = new JSONObject();
        result.put("jsonrpc", "2.0");
        result.put("id", id);

        try {
            if ("initialize".equals(method)) {
                JSONObject params = request.getJSONObject("params");
                String protocolVersion = params != null ? params.getString("protocolVersion") : MCP_VERSION;

                JSONObject serverInfo = new JSONObject();
                serverInfo.put("name", SERVER_NAME);
                serverInfo.put("version", SERVER_VERSION);

                JSONObject capabilities = new JSONObject();
                JSONObject toolsCapability = new JSONObject();
                toolsCapability.put("listChanged", true);
                capabilities.put("tools", toolsCapability);

                JSONObject resultData = new JSONObject();
                resultData.put("protocolVersion", MCP_VERSION);
                resultData.put("serverInfo", serverInfo);
                resultData.put("capabilities", capabilities);

                result.put("result", resultData);
            } else if ("notifications/initialized".equals(method)) {
                result.put("result", new JSONObject());
            } else if ("tools/list".equals(method)) {
                result.put("result", getToolsList());
            } else if ("tools/call".equals(method)) {
                JSONObject params = request.getJSONObject("params");
                String toolName = params.getString("name");
                JSONObject arguments = params.getJSONObject("arguments");
                result.put("result", callTool(toolName, arguments));
            } else {
                JSONObject error = new JSONObject();
                error.put("code", -32601);
                error.put("message", "Method not found: " + method);
                result.put("error", error);
            }
        } catch (Exception e) {
            log.error("MCP 请求处理失败", e);
            JSONObject error = new JSONObject();
            error.put("code", -32603);
            error.put("message", "Internal error: " + e.getMessage());
            result.put("error", error);
        }

        String jsonStr = result.toJSONString();
        response.getWriter().write(jsonStr);
        response.getWriter().flush();
    }

    private JSONObject getToolsList() {
        JSONArray tools = new JSONArray();

        tools.add(buildToolSpec("leave_list",
            "查询所有请假记录列表，支持按用户名、请假类型、审批状态筛选和分页",
            buildParameters(new String[][]{
                {"userName", "string", "用户名（模糊查询）", "false"},
                {"leaveType", "string", "请假类型：sick(病假) personal(事假) maternity(产假) offshift(调休)", "false"},
                {"leaveStatus", "string", "审批状态：0(刚提交) 1(审核通过) 2(被退回) 3(已销假)", "false"},
                {"page", "integer", "页码，默认1", "false"},
                {"pageSize", "integer", "每页条数，默认20", "false"}
            })));

        tools.add(buildToolSpec("leave_my",
            "查询指定用户的请假记录",
            buildParameters(new String[][]{
                {"userId", "string", "用户ID", "true"},
                {"leaveType", "string", "请假类型：sick(病假) personal(事假) maternity(产假) offshift(调休)", "false"},
                {"leaveStatus", "string", "审批状态：0(刚提交) 1(审核通过) 2(被退回) 3(已销假)", "false"},
                {"page", "integer", "页码，默认1", "false"},
                {"pageSize", "integer", "每页条数，默认20", "false"}
            })));

        tools.add(buildToolSpec("leave_detail",
            "根据ID查询请假详情",
            buildParameters(new String[][]{
                {"id", "string", "请假记录ID", "true"}
            })));

        tools.add(buildToolSpec("leave_add",
            "新增请假申请",
            buildParameters(new String[][]{
                {"userId", "string", "用户ID", "true"},
                {"leaveType", "string", "请假类型：sick(病假) personal(事假) maternity(产假) offshift(调休)", "true"},
                {"startTime", "string", "请假开始时间，格式：yyyy-MM-dd HH:mm:ss", "true"},
                {"endTime", "string", "请假结束时间，格式：yyyy-MM-dd HH:mm:ss", "true"},
                {"leaveReason", "string", "请假原因（最多200字）", "false"}
            })));

        tools.add(buildToolSpec("leave_update",
            "修改请假申请",
            buildParameters(new String[][]{
                {"id", "string", "请假记录ID", "true"},
                {"leaveType", "string", "请假类型：sick(病假) personal(事假) maternity(产假) offshift(调休)", "false"},
                {"startTime", "string", "请假开始时间，格式：yyyy-MM-dd HH:mm:ss", "false"},
                {"endTime", "string", "请假结束时间，格式：yyyy-MM-dd HH:mm:ss", "false"},
                {"leaveReason", "string", "请假原因（最多200字）", "false"}
            })));

        tools.add(buildToolSpec("leave_delete",
            "删除请假记录",
            buildParameters(new String[][]{
                {"id", "string", "请假记录ID", "true"}
            })));

        tools.add(buildToolSpec("leave_cancel",
            "销假操作，将请假状态改为已销假",
            buildParameters(new String[][]{
                {"id", "string", "请假记录ID", "true"}
            })));

        JSONObject result = new JSONObject();
        result.put("tools", tools);
        return result;
    }

    private JSONObject callTool(String toolName, JSONObject arguments) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> resultMap;

        if ("leave_list".equals(toolName)) {
            String userName = getString(arguments, "userName");
            String leaveType = getString(arguments, "leaveType");
            String leaveStatus = getString(arguments, "leaveStatus");
            int page = getInt(arguments, "page", 1);
            int pageSize = getInt(arguments, "pageSize", 20);
            resultMap = leaveMcpService.list(userName, leaveType, leaveStatus, page, pageSize);
        } else if ("leave_my".equals(toolName)) {
            String userId = getString(arguments, "userId");
            String leaveType = getString(arguments, "leaveType");
            String leaveStatus = getString(arguments, "leaveStatus");
            int page = getInt(arguments, "page", 1);
            int pageSize = getInt(arguments, "pageSize", 20);
            resultMap = leaveMcpService.myList(userId, leaveType, leaveStatus, page, pageSize);
        } else if ("leave_detail".equals(toolName)) {
            String id = getString(arguments, "id");
            resultMap = leaveMcpService.detail(id);
        } else if ("leave_add".equals(toolName)) {
            String userId = getString(arguments, "userId");
            String leaveType = getString(arguments, "leaveType");
            Date startTime = sdf.parse(getString(arguments, "startTime"));
            Date endTime = sdf.parse(getString(arguments, "endTime"));
            String leaveReason = getString(arguments, "leaveReason");
            resultMap = leaveMcpService.add(userId, leaveType, startTime, endTime, leaveReason);
        } else if ("leave_update".equals(toolName)) {
            String id = getString(arguments, "id");
            String leaveType = getString(arguments, "leaveType");
            String startTimeStr = getString(arguments, "startTime");
            String endTimeStr = getString(arguments, "endTime");
            Date startTime = startTimeStr != null ? sdf.parse(startTimeStr) : null;
            Date endTime = endTimeStr != null ? sdf.parse(endTimeStr) : null;
            String leaveReason = getString(arguments, "leaveReason");
            resultMap = leaveMcpService.update(id, leaveType, startTime, endTime, leaveReason);
        } else if ("leave_delete".equals(toolName)) {
            String id = getString(arguments, "id");
            resultMap = leaveMcpService.delete(id);
        } else if ("leave_cancel".equals(toolName)) {
            String id = getString(arguments, "id");
            resultMap = leaveMcpService.cancel(id);
        } else {
            JSONObject error = new JSONObject();
            error.put("code", -32602);
            error.put("message", "Unknown tool: " + toolName);
            JSONObject result = new JSONObject();
            result.put("error", error);
            return result;
        }

        JSONArray content = new JSONArray();
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", JSON.toJSONString(resultMap));
        content.add(textContent);

        JSONObject result = new JSONObject();
        result.put("content", content);
        return result;
    }

    private JSONObject buildToolSpec(String name, String description, JSONObject parameters) {
        JSONObject tool = new JSONObject();
        tool.put("name", name);
        tool.put("description", description);
        tool.put("inputSchema", parameters);
        return tool;
    }

    private JSONObject buildParameters(String[][] params) {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        JSONArray required = new JSONArray();

        for (String[] param : params) {
            JSONObject prop = new JSONObject();
            prop.put("type", param[1]);
            prop.put("description", param[2]);
            properties.put(param[0], prop);
            if ("true".equals(param[3])) {
                required.add(param[0]);
            }
        }

        schema.put("properties", properties);
        schema.put("required", required);
        return schema;
    }

    private String getString(JSONObject obj, String key) {
        if (obj == null || !obj.containsKey(key) || obj.get(key) == null) {
            return null;
        }
        return obj.getString(key);
    }

    private int getInt(JSONObject obj, String key, int defaultValue) {
        if (obj == null || !obj.containsKey(key) || obj.get(key) == null) {
            return defaultValue;
        }
        return obj.getIntValue(key);
    }
}
