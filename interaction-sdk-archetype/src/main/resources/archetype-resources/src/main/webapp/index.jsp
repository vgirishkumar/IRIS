<%
String myService = request.getScheme() + "://" 
    + request.getServerName() + ":" + request.getServerPort() 
    + request.getContextPath() + "/${artifactId}.svc/"; 
response.sendRedirect("explorer.jsp?odata-svc=" + myService);
%>