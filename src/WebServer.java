import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebServer {
    private final Database database;
    private final SQLParser parser;
    private HttpServer server;

    public WebServer(Database database, int port) throws IOException {
        this.database = database;
        this.parser = new SQLParser(database);
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        setupRoutes();
    }

    private void setupRoutes() {
        server.createContext("/", this::handleRoot);
        server.createContext("/api/users", this::handleUsers);
        server.createContext("/api/sql", this::handleSQL);
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("Web server started on port " + server.getAddress().getPort());
    }

    private void sendResponse(HttpExchange exchange, int code, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(code, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html><html><head><title>Simple RDBMS Demo</title><style>" +
            "body{font-family:Arial;max-width:800px;margin:50px auto;padding:20px;}" +
            "input,button{padding:8px;margin:5px;}" +
            "table{border-collapse:collapse;width:100%;margin:20px 0;}" +
            "th,td{border:1px solid #ddd;padding:8px;text-align:left;}" +
            "th{background-color:#4CAF50;color:white;}" +
            ".section{margin:30px 0;padding:20px;border:1px solid #ddd;}" +
            "</style></head><body><h1>Simple RDBMS Demo - User Management</h1>" +
            "<div class='section'><h2>Create User</h2>" +
            "<input type='number' id='userId' placeholder='User ID'>" +
            "<input type='text' id='userName' placeholder='Name'>" +
            "<input type='text' id='userEmail' placeholder='Email'>" +
            "<button onclick='createUser()'>Create</button></div>" +
            "<div class='section'><h2>Users List</h2>" +
            "<button onclick='loadUsers()'>Refresh</button>" +
            "<div id='usersList'></div></div>" +
            "<div class='section'><h2>SQL Console</h2>" +
            "<textarea id='sqlQuery' rows='4' style='width:100%;' placeholder='Enter SQL query...'></textarea><br>" +
            "<button onclick='executeSQL()'>Execute</button>" +
            "<pre id='sqlResult'></pre></div><script>" +
            "function createUser(){" +
            "const id=document.getElementById('userId').value;" +
            "const name=document.getElementById('userName').value;" +
            "const email=document.getElementById('userEmail').value;" +
            "fetch('/api/users',{method:'POST',headers:{'Content-Type':'application/json'}," +
            "body:JSON.stringify({id:parseInt(id),name,email})})" +
            ".then(r=>r.text()).then(data=>{alert(data);loadUsers();});}" +
            "function loadUsers(){fetch('/api/users').then(r=>r.json()).then(users=>{" +
            "let html='<table><tr><th>ID</th><th>Name</th><th>Email</th><th>Actions</th></tr>';" +
            "users.forEach(u=>{html+='<tr><td>'+u.id+'</td><td>'+u.name+'</td><td>'+u.email+'</td>';" +
            "html+='<td><button onclick=\"deleteUser('+u.id+')\">Delete</button></td></tr>';});" +
            "html+='</table>';document.getElementById('usersList').innerHTML=html;});}" +
            "function deleteUser(id){if(confirm('Delete user '+id+'?')){" +
            "fetch('/api/users?id='+id,{method:'DELETE'})" +
            ".then(r=>r.text()).then(data=>{alert(data);loadUsers();});}}" +
            "function executeSQL(){const query=document.getElementById('sqlQuery').value;" +
            "fetch('/api/sql',{method:'POST',headers:{'Content-Type':'text/plain'},body:query})" +
            ".then(r=>r.text()).then(result=>{document.getElementById('sqlResult').textContent=result;});}" +
            "loadUsers();</script></body></html>";
        sendResponse(exchange, 200, html, "text/html");
    }

    private void handleSQL(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")) {
            String sql = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String result = parser.execute(sql);
            sendResponse(exchange, 200, result, "text/plain");
        }
    }

    private void handleUsers(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        try {
            if (method.equals("GET")) {
                Table table = database.getTable("users");
                List<Row> rows = table.select(null);
                
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < rows.size(); i++) {
                    Row row = rows.get(i);
                    json.append(String.format("{\"id\":%s,\"name\":\"%s\",\"email\":\"%s\"}",
                        row.get("id"), row.get("name"), row.get("email")));
                    if (i < rows.size() - 1) json.append(",");
                }
                json.append("]");
                
                sendResponse(exchange, 200, json.toString(), "application/json");
                
            } else if (method.equals("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String[] parts = body.replaceAll("[{}\"]", "").split(",");
                
                Map<String, String> data = new HashMap<>();
                for (String part : parts) {
                    String[] kv = part.split(":");
                    data.put(kv[0].trim(), kv[1].trim());
                }
                
                String sql = String.format("INSERT INTO users (id, name, email) VALUES (%s, '%s', '%s')",
                    data.get("id"), data.get("name"), data.get("email"));
                String result = parser.execute(sql);
                
                sendResponse(exchange, 200, result, "text/plain");
                
            } else if (method.equals("DELETE")) {
                String query = exchange.getRequestURI().getQuery();
                String id = query.split("=")[1];
                
                String sql = "DELETE FROM users WHERE id=" + id;
                String result = parser.execute(sql);
                
                sendResponse(exchange, 200, result, "text/plain");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error: " + e.getMessage(), "text/plain");
        }
    }
}
