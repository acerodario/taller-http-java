package com.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

@Component
public class TraceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if ("TRACE".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");

            Map<String, Object> info = new LinkedHashMap<>();
            info.put("metodo", req.getMethod());
            info.put("uri", req.getRequestURI());

            Map<String, String> headers = new LinkedHashMap<>();
            Collections.list(req.getHeaderNames())
                    .forEach(name -> headers.put(name, req.getHeader(name)));

            info.put("headers", headers);
            info.put("descripcion", "Echo del request TRACE manejado manualmente por TraceFilter");

            String json = new ObjectMapper().writeValueAsString(info);
            res.getWriter().write(json);
            res.getWriter().flush();
            return;
        }

        chain.doFilter(request, response);
    }
}
