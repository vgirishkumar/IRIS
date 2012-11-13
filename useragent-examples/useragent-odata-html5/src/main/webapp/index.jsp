<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>DataJS CRUD Demo</title>
    <link href="Styles/Site.css" rel="stylesheet" type="text/css" />
    <link type="text/css" rel="Stylesheet" href="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.8.10/themes/redmond/jquery-ui.css" /> 
</head>
<body>
    
    <div class="page">
        <div class="header">
            <div class="title">
                <h1>
                    DataJS CRUD Demo
                </h1>
            </div>
            <div class="loginDisplay"></div>
            <div class="clear hideSkiplink"></div>
        </div>
		<div class="leftCol">
            <div id="service-root" class="ui-widget">
	            <table id="services" class="ui-widget ui-widget-content">
			            <tr class="ui-widget-header ">
				            <th>EntitySet</th>
			            </tr>
	            </table>
                <span id="loadingServices" style="display:none">Loading...</span>
            </div>
		</div>
        <div class="main">
            <div id="dialog-form" title="Create new user">
	            <p class="validateTips">All form fields are required.</p>
	            <form>
	            <fieldset>
		            <label for="name">Name</label>
		            <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
		            <label for="email">Email</label>
		            <input type="text" name="email" id="email" value="" class="text ui-widget-content ui-corner-all" />
		            <label for="password">Password</label>
		            <input type="password" name="password" id="password" value="" class="text ui-widget-content ui-corner-all" />
                    <div id="loading" style="display:none">Loading...</div>
	            </fieldset>
	            </form>
            </div>

            <div id="users-contain" class="ui-widget">
	            <h1 id="EntitySetName">Select EntitySet</h1>
                <button id="createEntity" style="display:none">Create new item</button>
	            <table id="entities" class="ui-widget ui-widget-content">
                    <tr class="ui-widget-header "></tr>
	            </table>
                <span id="loadingEntities" style="display:none">Loading...</span>
            </div>
        </div>
        <div class="clear"></div>
        <div class="footer"></div>
    </div>
    <script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery/jquery-1.5.1.min.js"></script>
    <script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.8.13/jquery-ui.min.js"></script>
    <script type="text/javascript" src="Scripts/datajs-1.0.3.js"></script>
    <script type="text/javascript" src="Scripts/DataJSCRUD.js"></script>
    <script type="text/javascript">
        $(OnPageLoad)
    </script>
</body>
</html>
























