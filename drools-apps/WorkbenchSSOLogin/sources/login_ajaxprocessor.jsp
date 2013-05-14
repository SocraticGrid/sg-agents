<%@ page import="org.wso2.carbon.identity.sso.saml.ui.SAMLSSOProviderConstants" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar"
           prefix="carbon" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    
    <title>About Clinical Decision Support Workbench</title>
                       <!--JQuery-->

    <!--CSS-->
    
    <script type="text/javascript">

if (document.layers)
    document.captureEvents(Event.KEYDOWN);
document.onkeydown =
    function (evt) {
        var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
        if (keyCode == 13) { 
            document.getElementById('loginForm').submit();
        }
        if (keyCode == 27) { 
            //For escape.
            //Your function here.
        }
        else
            return true;
    };


function dispDate(dateVal) {
DaystoAdd=dateVal
TodaysDate = new Date();
TodaysDay = new Array('Sunday', 'Monday', 'Tuesday','Wednesday', 'Thursday', 'Friday', 'Saturday');
TodaysMonth = new Array('January', 'February', 'March','April', 'May','June', 'July', 'August', 'September','October', 'November', 'December');
DaysinMonth = new Array('31', '28', '31', '30', '31', '30', '31', '31', '30', '31', '30', '31');
function LeapYearTest (Year) {
if (((Year % 400)==0) || (((Year % 100)!=0) && (Year % 4)==0)) {
return true;
}
else {
return false;
}
}
CurrentYear = TodaysDate.getYear();
if (CurrentYear < 2000) 
CurrentYear = CurrentYear + 1900;
currentMonth = TodaysDate.getMonth();
DayOffset = TodaysDate.getDay();
currentDay = TodaysDate.getDate();
month = TodaysMonth[currentMonth];
if (month == 'February') {
if (((CurrentYear % 4)==0) && ((CurrentYear % 100)!=0) || ((CurrentYear % 400)==0)) {
DaysinMonth[1] = 29;
}
else {
DaysinMonth[1] = 28;
}
}
days = DaysinMonth[currentMonth];
currentDay += DaystoAdd;
if (currentDay > days) {
if (currentMonth == 11) {
currentMonth = 0;
month = TodaysMonth[currentMonth];
CurrentYear = CurrentYear + 1
}
else {
month =
TodaysMonth[currentMonth+1];
}
currentDay = currentDay - days;
}
DayOffset += DaystoAdd;
function offsettheDate (offsetCurrentDay) {
if (offsetCurrentDay > 6) {
offsetCurrentDay -= 6;
DayOffset = TodaysDay[offsetCurrentDay-1];
offsettheDate(offsetCurrentDay-1);
}
else {
DayOffset = TodaysDay[offsetCurrentDay];
return true;
}
}
offsettheDate(DayOffset);

TheDate = "";
//TheDate += DayOffset + ', ';
TheDate += month + ' ';
TheDate += currentDay + ', '; 
if (CurrentYear<100) CurrentYear="19" + CurrentYear;
TheDate += CurrentYear;
document.write(' '+TheDate);
}
</script>
    
<link rel="stylesheet" type="text/css" href="../carbon/sso-saml/index.css" media="all">
</head>
<body>
    <div id="suiteHeader" class="primaryBG">
        <div id="homeHeader" class="homeContent" style="height: 110px; background: url('../carbon/sso-saml/Sirona-CDSW-Logo.png') no-repeat scroll 0% 0% transparent;">
            <div id="suiteHeaderTop" class="homeContent">
                <span id="debug"></span>                
                <span id="suiteDate"><script type="text/javascript">dispDate(0);</script></span>
            </div>
            <div id="homeTopTabs">



<!-- Script to load data should be at the end of the HTML so that DOM is loaded -->


</div>

            <div id="homeSearch">
                <label for="homeSearchVal"></label><input id="homeSearchVal" value="Search" type="text">
                <img src="../carbon/sso-saml/magnifying-glass.png">
            </div>

            <div id="homeFonts">
                <a href="#" style="padding-right: 15px;">Full Screen</a>
                <span><a href="#">A</a><span>|</span><span><a href="#" style="font-size: 18px;">A</a></span></span>
            </div>
        </div>
    </div>

    <div id="homeContent">
        <table class="tblClean tblCenter" style="width: 100%;">
            <tbody><tr>
                <td><div id="homeNav">


    <ul id="suiteLeftNav"></ul>
    <!--<div id="pmrNavigation"></div>-->

    

</div></td>
                <td style="width: 100%; text-align: right;"><div id="homeBody">


<div id="loginMain">
    <div class="homeContent" style="height: 328px; background: url('../carbon/sso-saml/home-page-image.jpg') no-repeat scroll right top transparent;">
        <div id="loginInput" style="width: 49%;">
	    <form action="../samlsso" method="post" id="loginForm">            
            <table class="tblClean tblCenter inputTable primaryLinks">
                <tbody><tr><td colspan="3" style="padding: 95px 0pt 15px;">
                    <div style="text-align: center;"><span style="font-family: serif; font-size: 38px; padding-bottom: 30px;" class="primaryColor">
                        Partners in Health</span><br>
                        <span> For patients and health care providers,<br>vitae vel posuere tellus etiam</span>
                    </div>
                </td></tr>
                <tr><td style="text-align: right;"><label for="userName">Username</label></td><td><input type="text" id='username' name="username" size='30'/></td><td rowspan="3" style="vertical-align: middle;"><img id="formSubmit" src="../carbon/sso-saml/logintransparent.png" onclick="document.getElementById('loginForm').submit();"></td></tr>
                <tr><td style="text-align: right;"><label for="userPass">Password</label></td><td><input type="password" id='password' name="password" size='30'/>

		
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.ASSRTN_CONSUMER_URL %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.ASSRTN_CONSUMER_URL) %>"/>
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.ISSUER %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.ISSUER) %>"/>
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.REQ_ID %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.REQ_ID) %>"/>
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.SUBJECT %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.SUBJECT) %>"/>
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.RP_SESSION_ID %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.RP_SESSION_ID) %>"/>
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.ASSERTION_STR %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.ASSERTION_STR) %>"/>
                                            <input type="hidden" name="<%= SAMLSSOProviderConstants.RELAY_STATE %>"
                                                   value="<%= request.getAttribute(SAMLSSOProviderConstants.RELAY_STATE) %>"/>
					    
		</td></tr>
                
                <tr><td id="loginMessage" class="suiteErrorText" colspan="3"></td> </tr>

                <tr><td colspan="3"><a id="forgotPass" href="#">Request new password?</a><span style="padding: 0pt 10px;">|</span><a id="newAccount" href="#">Request an account</a></td></tr>
                <tr></tr>
            </tbody></table>
            </form>
        </div>
        <!--<div id="loginNews" style="width:50%"></div>-->
    </div>
</div>
<div id="miscInfo" class="homeContent" style="padding: 15px 0pt;">
    <table class="tblClean primaryLinks tblCenter" style="width: 960px;">
        <tbody><tr><td colspan="2" style="width: 50%;"></td><td colspan="2" style="width: 50%;"></td></tr>
        <tr><td></td><td style="line-height: 40px; padding-left: 20px;"><a href="javascript:void" style="font-size: 18px;">About Clinical Decision Support Workbench</a></td>
            <td></td><td style="line-height: 40px; padding-left: 20px;"><a href="javascript:void" style="font-size: 18px;">Recent Knowledge Management News</a></td></tr>
        <tr>
            <td id="aboutImage"><img src="../carbon/sso-saml/about-CDSW.jpg"></td><td id="aboutInfo" style="padding: 0pt 20px;"><a href="#" type="patientPortal">Patient portal</a> gives you easy access to your medical records online. Etiam magna nunc, faucibus sed euismod ut, varius varius tortor.<p><a href="#">Provider Portal</a> provides Administrative, EMR, and Optimization Service applications give providers powerful tools to help them serve their patients.</p><p><a href="#">Resource Capacity Simulator</a> nullam placerat dignissim tortor vitae varius. Quisque interdum ipsum eu mauris molestie auctor.</p></td>
            <td id="newsImage"><img src="../carbon/sso-saml/recent-news.jpg" style="padding-left: 20px; border-left: 1px solid rgb(204, 204, 204);"></td><td id="newsInfo" style="padding-left: 20px;"><a href="#" type="patientPortal">Exercise and Fitness</a> proin consectetur dui et lectus rhoncus blandit in sit amet eros. Pellentesque a ligula vel diam scelerisque tincidunt.<p><a href="#">Diet and Nutrition</a>  proin consectetur dui et lectus rhoncus blandit in sit amet eros. </p><p><a href="#">Veterans</a> nullam placerat dignissim tortor vitae varius. Quisque interdum ipsum eu mauris molestie auctor.</p></td>
        </tr>
    </tbody></table>
</div>

<!-- Scripts should be at the end of the HTML so that DOM is loaded -->


</div></td>
            </tr>
        </tbody></table>
    </div>

<div class="footer"></div>
</body>
</html>
