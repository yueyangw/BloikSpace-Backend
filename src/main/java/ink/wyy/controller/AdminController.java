package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.Commodity;
import ink.wyy.bean.User;
import ink.wyy.service.CommodityService;
import ink.wyy.service.UserService;
import ink.wyy.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    UserService userService;
    CommodityService commodityService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        commodityService = (CommodityService) getServletContext().getAttribute("commodityService");
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> request = JsonUtil.jsonToMap(req);
        if (request == null) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"请求有误\"}");
            return;
        }

        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin/setUserInfo":
                doSetUserInfo(req, resp, request);
                break;
            case "/admin/addUser":
                doAddUser(req, resp, request);
                break;
            case "/admin/deleteUser":
                doDeleteUser(req, resp, request);
                break;
            case "/admin/deleteCommodity":
                doDeleteCommodity(req, resp, request);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doSetUserInfo(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        User user = userService.AdminSetUserInfo(request);

        HashMap<String, Object> res = new HashMap<>();

        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            resp.getWriter().write(gson.toJson(res));
            return;
        }

        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }

    private void doAddUser(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        User user = userService.addUser(request);

        HashMap<String, Object> res = new HashMap<>();
        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            resp.getWriter().write(gson.toJson(res));
            return;
        }

        res.put("status", 1);
        res.put("id", user.getId());
        resp.getWriter().write(gson.toJson(res));
    }

    private void doDeleteUser(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String id = request.get("id");
        HashMap<String, Object> res = new HashMap<>();
        if (id == null || id.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        String msg = userService.delUser(Integer.valueOf(id));
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }


    private void doDeleteCommodity(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String id = request.get("id");
        String res = commodityService.delete(id, -1);
        resp.getWriter().write(res);
    }
}
