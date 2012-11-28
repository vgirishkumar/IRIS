<%
String myService = request.getScheme() + "://" 
    + request.getServerName() + ":" + request.getServerPort() 
    + request.getContextPath() + "/interaction-odata-notes.svc/"; 
response.sendRedirect("explorer.jsp?odata-svc=" + myService);
%>