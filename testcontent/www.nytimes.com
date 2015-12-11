<!DOCTYPE html>
<!--[if (gt IE 9)|!(IE)]> <!--> <html lang="en" class="no-js edition-domestic app-homepage"  itemscope xmlns:og="http://opengraphprotocol.org/schema/"> <!--<![endif]-->
<!--[if IE 9]> <html lang="en" class="no-js ie9 lt-ie10 edition-domestic app-homepage" xmlns:og="http://opengraphprotocol.org/schema/"> <![endif]-->
<!--[if IE 8]> <html lang="en" class="no-js ie8 lt-ie10 lt-ie9 edition-domestic app-homepage" xmlns:og="http://opengraphprotocol.org/schema/"> <![endif]-->
<!--[if (lt IE 8)]> <html lang="en" class="no-js lt-ie10 lt-ie9 lt-ie8 edition-domestic app-homepage" xmlns:og="http://opengraphprotocol.org/schema/"> <![endif]-->
<head>
    <title>The New York Times - Breaking News, World News & Multimedia</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" /><script type="text/javascript">window.NREUM||(NREUM={}),__nr_require=function(e,n,t){function r(t){if(!n[t]){var o=n[t]={exports:{}};e[t][0].call(o.exports,function(n){var o=e[t][1][n];return r(o?o:n)},o,o.exports)}return n[t].exports}if("function"==typeof __nr_require)return __nr_require;for(var o=0;o<t.length;o++)r(t[o]);return r}({QJf3ax:[function(e,n){function t(e){function n(n,t,a){e&&e(n,t,a),a||(a={});for(var u=c(n),f=u.length,s=i(a,o,r),p=0;f>p;p++)u[p].apply(s,t);return s}function a(e,n){f[e]=c(e).concat(n)}function c(e){return f[e]||[]}function u(){return t(n)}var f={};return{on:a,emit:n,create:u,listeners:c,_events:f}}function r(){return{}}var o="nr@context",i=e("gos");n.exports=t()},{gos:"7eSDFh"}],ee:[function(e,n){n.exports=e("QJf3ax")},{}],3:[function(e,n){function t(e){return function(){r(e,[(new Date).getTime()].concat(i(arguments)))}}var r=e("handle"),o=e(1),i=e(2);"undefined"==typeof window.newrelic&&(newrelic=window.NREUM);var a=["setPageViewName","addPageAction","setCustomAttribute","finished","addToTrace","inlineHit","noticeError"];o(a,function(e,n){window.NREUM[n]=t("api-"+n)}),n.exports=window.NREUM},{1:12,2:13,handle:"D5DuLP"}],gos:[function(e,n){n.exports=e("7eSDFh")},{}],"7eSDFh":[function(e,n){function t(e,n,t){if(r.call(e,n))return e[n];var o=t();if(Object.defineProperty&&Object.keys)try{return Object.defineProperty(e,n,{value:o,writable:!0,enumerable:!1}),o}catch(i){}return e[n]=o,o}var r=Object.prototype.hasOwnProperty;n.exports=t},{}],D5DuLP:[function(e,n){function t(e,n,t){return r.listeners(e).length?r.emit(e,n,t):void(r.q&&(r.q[e]||(r.q[e]=[]),r.q[e].push(n)))}var r=e("ee").create();n.exports=t,t.ee=r,r.q={}},{ee:"QJf3ax"}],handle:[function(e,n){n.exports=e("D5DuLP")},{}],XL7HBI:[function(e,n){function t(e){var n=typeof e;return!e||"object"!==n&&"function"!==n?-1:e===window?0:i(e,o,function(){return r++})}var r=1,o="nr@id",i=e("gos");n.exports=t},{gos:"7eSDFh"}],id:[function(e,n){n.exports=e("XL7HBI")},{}],G9z0Bl:[function(e,n){function t(){var e=d.info=NREUM.info,n=f.getElementsByTagName("script")[0];if(e&&e.licenseKey&&e.applicationID&&n){c(p,function(n,t){n in e||(e[n]=t)});var t="https"===s.split(":")[0]||e.sslForHttp;d.proto=t?"https://":"http://",a("mark",["onload",i()]);var r=f.createElement("script");r.src=d.proto+e.agent,n.parentNode.insertBefore(r,n)}}function r(){"complete"===f.readyState&&o()}function o(){a("mark",["domContent",i()])}function i(){return(new Date).getTime()}var a=e("handle"),c=e(1),u=window,f=u.document;e(2);var s=(""+location).split("?")[0],p={beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",agent:"js-agent.newrelic.com/nr-686.min.js"},d=n.exports={offset:i(),origin:s,features:{}};f.addEventListener?(f.addEventListener("DOMContentLoaded",o,!1),u.addEventListener("load",t,!1)):(f.attachEvent("onreadystatechange",r),u.attachEvent("onload",t)),a("mark",["firstbyte",i()])},{1:12,2:3,handle:"D5DuLP"}],loader:[function(e,n){n.exports=e("G9z0Bl")},{}],12:[function(e,n){function t(e,n){var t=[],o="",i=0;for(o in e)r.call(e,o)&&(t[i]=n(o,e[o]),i+=1);return t}var r=Object.prototype.hasOwnProperty;n.exports=t},{}],13:[function(e,n){function t(e,n,t){n||(n=0),"undefined"==typeof t&&(t=e?e.length:0);for(var r=-1,o=t-n||0,i=Array(0>o?0:o);++r<o;)i[r]=e[n+r];return i}n.exports=t},{}]},{},["G9z0Bl"]);</script>
<link rel="shortcut icon" href="http://static01.nyt.com/favicon.ico" />
<link rel="apple-touch-icon-precomposed" sizes="144×144" href="http://static01.nyt.com/images/icons/ios-ipad-144x144.png" />
<link rel="apple-touch-icon-precomposed" sizes="114×114" href="http://static01.nyt.com/images/icons/ios-iphone-114x144.png" />
<link rel="apple-touch-icon-precomposed" href="http://static01.nyt.com/images/icons/ios-default-homescreen-57x57.png" />
<meta name="sourceApp" content="nyt-v5" />
<meta id="applicationName" name="applicationName" content="homepage" />
<meta id="foundation-build-id" name="foundation-build-id" content="" />
<link rel="canonical" href="http://www.nytimes.com" />
<link rel="alternate" type="application/rss+xml" title="RSS" href="http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml" />
<link rel="alternate" media="handheld" href="http://mobile.nytimes.com" />
<meta name="robots" content="noarchive,noodp,noydir" />
<meta name="description" content="The New York Times: Find breaking news, multimedia, reviews & opinion on Washington, business, sports, movies, travel, books, jobs, education, real estate, cars & more at nytimes.com." />
<meta name="CG" content="Homepage" />
<meta name="SCG" content="" />
<meta name="PT" content="Homepage" />
<meta name="PST" content="" />
<meta name="application-name" content="The New York Times" />
<meta name="msapplication-starturl" content="http://www.nytimes.com" />
<meta name="msapplication-task" content="name=Search;action-uri=http://query.nytimes.com/search/sitesearch?src=iepin;icon-uri=http://css.nyt.com/images/icons/search.ico" />
<meta name="msapplication-task" content="name=Most Popular;action-uri=http://www.nytimes.com/gst/mostpopular.html?src=iepin;icon-uri=http://css.nyt.com/images/icons/mostpopular.ico" />
<meta name="msapplication-task" content="name=Video;action-uri=http://video.nytimes.com/?src=iepin;icon-uri=http://css.nyt.com/images/icons/video.ico" />
<meta name="msapplication-task" content="name=Homepage;action-uri=http://www.nytimes.com?src=iepin&amp;adxnnl=1;icon-uri=http://css.nyt.com/images/icons/homepage.ico" />
<meta property="og:url" content="http://www.nytimes.com" />
<meta property="og:type" content="website" />
<meta property="og:title" content="Breaking News, World News & Multimedia" />
<meta property="og:description" content="The New York Times: Find breaking news, multimedia, reviews & opinion on Washington, business, sports, movies, travel, books, jobs, education, real estate, cars & more at nytimes.com." />
<meta property="og:image" content="http://static01.nyt.com/images/icons/t_logo_291_black.png" />
<meta property="fb:app_id" content="9869919170" />
<meta name="apple-itunes-app" content="app-id=357066198, affiliate-data=at=10lIEQ&ct=Web%20iPad%20Smart%20App%20Banner&pt=13036" />
<meta name="keywords" content="Gun Control,Malloy, Dannel P,Connecticut,Gun Control,Terrorism,School Shootings and Armed Attacks,United States Defense and Military Forces,Terrorism,Classified Information and State Secrets,Islamic State in Iraq and Syria (ISIS),Defense Department,Military Bases and Installations,Al Qaeda,Boko Haram,Africa,Central Asia,Yemen,Middle East,Fuel Emissions (Transportation),Greenhouse Gas Emissions,Automobiles,Ethics and Official Misconduct,Environmental Protection Agency,Volkswagen AG,Germany,United States,Wolfsburg (Germany),China,Jackson Hole (Wyo),Beijing (China),Hebei Province (China),United States,Seizures (Medical),Children and Childhood,Drugs (Pharmaceuticals),Mental Health and Disorders,Depression (Mental),Epilepsy,Psychiatry and Psychiatrists,Antidepressants,Doctors,Bipolar Disorder,Attention Deficit Hyperactivity Disorder,American Academy of Pediatrics,Real Estate and Housing (Residential),de Blasio, Bill,Affordable Housing,Zoning,City Planning Commission (NYC),Movies,Forests and Forestry,Greenhouse Gas Emissions,United Nations Framework Convention on Climate Change,Brazil,Land Use Policies,Rainforest Action Network,Walt Disney Company,Kerry, John,Global Warming,United Nations,United States Politics and Government,United States International Relations,Greenhouse Gas Emissions,United Nations Framework Convention on Climate Change,United States,China,Protective Clothing and Gear,Masks,Counterfeit Merchandise,Air Pollution,3M Company,China,United Nations Framework Convention on Climate Change,Global Warming,Carbon Dioxide,Serial (Radio Program),Podcasts,Bergdahl, Bowe R,Afghanistan War (2001-14),Argentina,Appointments and Executive Changes,Macri, Mauricio,Kirchner, Cristina Fernandez de,Boycotts,Skelos, Adam B (1982- ),Skelos, Dean G,Bribery and Kickbacks,Extortion and Blackmail,New York State,Wood, Kimba M,Astrology,Families and Family Life,India,Love (Emotion),Dating and Relationships,Hinduism,Labor and Jobs,Freelancing, Self-Employment and Independent Contracting,Instacart,Munchery Inc,National Labor Relations Board,Lyft Inc,Uber Technologies Inc,United States,Customs, Etiquette and Manners,Children and Childhood,Youth" />
<meta name="video:playerId" content="2640832222001" />
<meta name="video:publisherId" content="1749339200" />
<meta name="video:publisherReadToken" content="cE97ArV7TzqBzkmeRVVhJ8O6GWME2iG_bRvjBTlNb4o." />
<meta name="dfp-ad-unit-path" content="homepage/us" />
<meta name="dfp-amazon-enabled" content="false" />
<meta name="adxPage" content="homepage.nytimes.com/index.html" />
    
            <!--[if (gt IE 9)|!(IE)]> <!-->
    <link rel="stylesheet" type="text/css" media="screen" href="http://a1.nyt.com/assets/homepage/20151207-151834/css/homepage/styles.css" />
<!--<![endif]-->
<!--[if lte IE 9]>
    <link rel="stylesheet" type="text/css" media="screen" href="http://a1.nyt.com/assets/homepage/20151207-151834/css/homepage/styles-ie.css" />
<![endif]-->
        <script type="text/javascript">var googletag=googletag||{};googletag.cmd=googletag.cmd||[],function(){var t=document.createElement("script");t.async=!0,t.type="text/javascript";var e="https:"==document.location.protocol;t.src=(e?"https:":"http:")+"//www.googletagservices.com/tag/js/gpt.js";var o=document.getElementsByTagName("script")[0];o.parentNode.insertBefore(t,o)}();</script>
<script src="//typeface.nytimes.com/zam5nzz.js"></script>
<script>try{Typekit.load();}catch(e){}</script>
<script src="//cdn.optimizely.com/js/3338050995.js"></script>

<script id="abtestconfig" type="application/json">

[
    {
        "testId": "0012",
        "testName": "tallWatchingModule",
        "throttle": 1.0,
        "allocation": 0.9,
        "variants": 1,
        "applications": ["homepage"]
    },
    {
        "testId": "0033",
        "testName": "recommendedLabelTest",
        "throttle": 1,
        "allocation": 0.833,
        "variants": 5,
        "applications": ["article"]
    },
    {
        "testId": "0036",
        "testName": "velcroSocialFollow",
        "throttle": 0.1,
        "allocation": 0.5,
        "variants": 1,
        "applications": ["article", "homepage"]
    },
    {
        "testId": "0051",
        "testName": "shuffleRecommendations",
        "throttle": 1.0,
        "allocation": 0.667,
        "variants": 1,
        "applications": ["article"]
    },
    {
        "testId": "0052",
        "testName": "paidPostDriver",
        "throttle": 1.0,
        "allocation": 0.875,
        "variants": 7,
        "applications": ["article"]
    },
    {
        "testId": "0061",
        "testName": "paidPostFivePackMock",
        "throttle": 0,
        "allocation": 0,
        "variants": 1,
        "applications": ["homepage"]
    },
    {
        "testId": "0063",
        "testName": "paidPostFivePack",
        "throttle": 1,
        "allocation": 0.5,
        "variants": 1,
        "applications": ["homepage"]
    },
    {
        "testId": "0064",
        "testName": "realEstateSearch",
        "throttle": 1,
        "allocation": 1,
        "variants": 1,
        "applications": ["realestate", "article"]
     },
     {
         "testId": "0066",
         "testName": "ribbonChartbeatMostEmailed",
         "throttle": 1,
         "allocation": 0.5,
         "variants": 1,
         "applications": ["article"]
     },
     {
         "testId": "0067",
         "testName": "pinnedMasthead",
         "throttle": 0.02,
         "allocation": 0.5,
         "variants": 1,
         "applications": ["homepage"]
     },
     {
         "testId": "0069",
  	 "testName": "coloredSharetools",
         "throttle": 1,
         "allocation": 0.5,
         "variants": 1,
         "applications": ["slideshow"]
     }
]

</script>


<script id="user-info-data" type="application/json">
{ "meta": {},
  "data": {
    "id": "0",
    "name": "",
    "subscription": [],
    "demographics": {}
  }
}
</script>

<script>
var require = {
    baseUrl: 'http://a1.nyt.com/assets/',
    waitSeconds: 20,
    paths: {
        'foundation': 'homepage/20151207-151834/js/foundation',
        'shared': 'homepage/20151207-151834/js/shared',
        'homepage': 'homepage/20151207-151834/js/homepage',
        'application': 'homepage/20151207-151834/js/homepage/',
        'videoFactory': 'http://static01.nyt.com/js2/build/video/2.0/videofactoryrequire',
        'videoPlaylist': 'http://static01.nyt.com/js2/build/video/players/extended/2.0/appRequire',
        'auth/mtr': 'http://static01.nyt.com/js/mtr',
        'auth/growl': 'http://static01.nyt.com/js/auth/growl/default',
        'vhs': 'http://static01.nyt.com/video/vhs/build/vhs-2.x.min'
    }
};
</script>
<!--[if (gte IE 9)|!(IE)]> <!-->
<script data-main="foundation/main" src="http://a1.nyt.com/assets/homepage/20151207-151834/js/foundation/lib/framework.js"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script>
    require.map = { '*': { 'foundation/main': 'foundation/legacy_main' } };
</script>
<script data-main="foundation/legacy_main" src="http://a1.nyt.com/assets/homepage/20151207-151834/js/foundation/lib/framework.js"></script>
<![endif]-->
<script>
window.magnum.processFlags(["limitFabrikSave","moreFollowSuggestions","unfollowComments","homepageOpinionKickerCss","followFeature","allTheEmphases","videoVHSCover","videoVHSHomepageCover","additionalOpinionRegions","hpViewability","miniNavCount","newsEventHierarchy","freeTrial","insiderLaunch"]);
</script>
</head>
<body>
    
    <style>
    .lt-ie10 .messenger.suggestions {
        display: block !important;
        height: 50px;
    }

    .lt-ie10 .messenger.suggestions .message-bed {
        background-color: #f8e9d2;
        border-bottom: 1px solid #ccc;
    }

    .lt-ie10 .messenger.suggestions .message-container {
        padding: 11px 18px 11px 30px;
    }

    .lt-ie10 .messenger.suggestions .action-link {
        font-family: "nyt-franklin", arial, helvetica, sans-serif;
        font-size: 10px;
        font-weight: bold;
        color: #a81817;
        text-transform: uppercase;
    }

    .lt-ie10 .messenger.suggestions .alert-icon {
        background: url('http://i1.nyt.com/images/icons/icon-alert-12x12-a81817.png') no-repeat;
        width: 12px;
        height: 12px;
        display: inline-block;
        margin-top: -2px;
        float: none;
    }

    .lt-ie10 .masthead,
    .lt-ie10 .navigation,
    .lt-ie10 .comments-panel {
        margin-top: 50px !important;
    }

    .lt-ie10 .ribbon {
        margin-top: 97px !important;
    }
</style>
<div id="suggestions" class="suggestions messenger nocontent robots-nocontent" style="display:none;">
    <div class="message-bed">
        <div class="message-container last-message-container">
            <div class="message">
                <span class="message-content">
                    <i class="icon alert-icon"></i><span class="message-title">NYTimes.com no longer supports Internet Explorer 9 or earlier. Please upgrade your browser.</span>
                    <a href="http://www.nytimes.com/content/help/site/ie9-support.html" class="action-link">LEARN MORE »</a>
                </span>
            </div>
        </div>
    </div>
</div>

    <div id="shell" class="shell">
    <header id="masthead" class="masthead theme-pinned-masthead" role="banner">

    <div id="announcements-container" class="announcements-container"></div>

    <div id="Header1" class="ad header1-ad"></div>

    <div class="masthead-cap-container">

        <div id="masthead-cap" class="masthead-cap">

            <div class="quick-navigation button-group">

                <button class="button sections-button enable-a11y">
                    <i class="icon sprite-icon"></i><span class="button-text">Sections</span>
                </button>
                <button class="button search-button">
                    <i class="icon sprite-icon"></i><span class="button-text">Search</span>
                </button>
                <a class="button skip-button skip-to-content visually-hidden focusable" href="#top-news">Skip to content</a>
                <a class="button skip-button skip-to-navigation visually-hidden focusable" href="#site-index-navigation">Skip to navigation</a>
            </div><!-- close quick-navigation -->

            <div class="user-tools">

                <div id="Bar1" class="ad bar1-ad"></div>

                <div class="user-tools-button-group button-group">
                    <button class="button subscribe-button hidden" data-href="http://www.nytimes.com/subscriptions/Multiproduct/lp3004.html">Subscribe Now</button>
                    <button class="button login-button login-modal-trigger hidden">Log In</button>
                    <button class="button notifications-button hidden"><i class="icon sprite-icon"></i><span class="button-text">0</span></button>
                    <button class="button user-settings-button">
                        <i class="icon sprite-icon"></i><span class="button-text">Settings</span>
                    </button>
                </div>

            </div><!-- close user-tools -->

        </div><!-- close masthead-cap -->

    </div><!-- close masthead-cap-container -->

    <div class="masthead-meta">

        <div class="editions tab">

            <ul class="editions-menu">
                                    <li class="edition-domestic-toggle active">U.S.</li>
                    <li class="edition-international-toggle"><a href="http://international.nytimes.com" data-edition="global">International</a></li>
                
                <li class="edition-chinese-toggle"><a href="http://cn.nytimes.com" target="_blank" data-edition="chinese">中文</a></li>
            </ul>

        </div><!-- close editions -->

        <div id="TopLeft" class="ad top-left-ad"></div>
        <div id="TopRight" class="ad top-right-ad"></div>

        <h2 class="branding"><a href="http://www.nytimes.com/">
    <svg class="nyt-logo" width="379" height="64" role="img" aria-label="The New York Times">
        <image width="379" height="64" xlink:href="http://a1.nyt.com/assets/homepage/20151207-151834/images/foundation/logos/nyt-logo-379x64.svg" src="http://a1.nyt.com/assets/homepage/20151207-151834/images/foundation/logos/nyt-logo-379x64.png" alt="The New York Times" border="0"/>
    </svg>

</a></h2>
        <ul class="masthead-menu">
            <li class="date">Thursday, December 10, 2015</li><li class="todays-paper"><a href="http://www.nytimes.com/pages/todayspaper/index.html" data-collection="todays-paper"><i class="icon sprite-icon"></i>Today’s Paper</a></li><li class="video"><a href="http://www.nytimes.com/video" data-collection="video"><i class="icon sprite-icon"></i>Video</a></li><li id="weather" class="weather hidden" data-collection="weather"></li><li id="markets" class="markets hidden" data-collection="markets"></li>
        </ul>

    </div><!-- close masthead-meta -->

    <nav id="mini-navigation" class="mini-navigation">
    <ul class="mini-navigation-menu">

        <li>
            <button class="button sections-button">
                <i class="icon sprite-icon"></i>
                <span class="button-text">Sections</span>
            </button>
        </li><li>
            <button class="button search-button">
                <i class="icon sprite-icon"></i>
                <span class="button-text">Search</span>
            </button>
        </li>

            
                <li class="shortcuts-9A43D8FC-F4CF-44D9-9B34-138D30468F8F ">
                    <a href="http://www.nytimes.com/pages/world/index.html">World</a>
                </li>

            
                <li class="shortcuts-23FD6C8B-62D5-4CEA-A331-6C2A9A1223BE ">
                    <a href="http://www.nytimes.com/pages/national/index.html">U.S.</a>
                </li>

            
                <li class="shortcuts-80E6DEE6-87E4-4AD0-9152-14FA6B07E5AB ">
                    <a href="http://www.nytimes.com/pages/politics/index.html">Politics</a>
                </li>

            
                <li class="shortcuts-C4DC8C0C-E148-4201-BF10-82F1C903DBFB ">
                    <a href="http://www.nytimes.com/pages/nyregion/index.html">N.Y.</a>
                </li>

            
                <li class="shortcuts-104D1E63-9701-497B-8CF4-A4D120C9014E domestic">
                    <a href="http://www.nytimes.com/pages/business/index.html">Business</a>
                </li>

            
                <li class="shortcuts-A257D89A-0D3C-40AF-9C34-1A25A7947D94 international">
                    <a href="http://www.nytimes.com/pages/business/international/index.html">Business</a>
                </li>

            
                <li class="shortcuts-AD8090D7-4137-4D71-84C8-70DA3BD89778 domestic">
                    <a href="http://www.nytimes.com/pages/opinion/index.html">Opinion</a>
                </li>

            
                <li class="shortcuts-09736473-CB3F-4B2F-9772-3AF128ABE12D international">
                    <a href="http://www.nytimes.com/pages/opinion/international/index.html">Opinion</a>
                </li>

            
                <li class="shortcuts-78FBAD45-31A9-4EC7-B172-7D62A2B9955E ">
                    <a href="http://www.nytimes.com/pages/technology/index.html">Tech</a>
                </li>

            
                <li class="shortcuts-A4B35924-DB6C-4EA3-997D-450810F4FEE6 ">
                    <a href="http://www.nytimes.com/section/science">Science</a>
                </li>

            
                <li class="shortcuts-7D6BE1AF-8CD8-430B-8B2A-17CD0EAA99AC ">
                    <a href="http://www.nytimes.com/pages/health/index.html">Health</a>
                </li>

            
                <li class="shortcuts-DE2B278B-2783-4506-AAD5-C15A5BB6DA1A domestic">
                    <a href="http://www.nytimes.com/pages/sports/index.html">Sports</a>
                </li>

            
                <li class="shortcuts-BE66F420-C51B-461D-B487-CACF62E94AAE international">
                    <a href="http://www.nytimes.com/pages/sports/international/index.html">Sports</a>
                </li>

            
                <li class="shortcuts-C5BFA7D5-359C-427B-90E6-6B7245A6CDD8 domestic">
                    <a href="http://www.nytimes.com/pages/arts/index.html">Arts</a>
                </li>

            
                <li class="shortcuts-0202D0E4-C59B-479A-BD42-6F1766459781 international">
                    <a href="http://www.nytimes.com/pages/arts/international/index.html">Arts</a>
                </li>

            
                <li class="shortcuts-B3DFBD82-F298-43B3-9458-219B4F6AA2A5 domestic">
                    <a href="http://www.nytimes.com/pages/fashion/index.html">Style</a>
                </li>

            
                <li class="shortcuts-CC9E2674-F6C4-4A39-813B-F5AB0C515CEA international">
                    <a href="http://www.nytimes.com/pages/style/international/index.html">Style</a>
                </li>

            
                <li class="shortcuts-D9C94A2B-0364-4D25-8383-592CC66F82D4 domestic">
                    <a href="http://www.nytimes.com/pages/dining/index.html">Food</a>
                </li>

            
                <li class="shortcuts-FDEFB811-B483-4C3D-A25A-FD07BE5EAD96 international">
                    <a href="http://www.nytimes.com/pages/dining/international/index.html">Food</a>
                </li>

            
                <li class="shortcuts-FDA10AC4-4738-4099-91E8-15584765C8D7 ">
                    <a href="http://www.nytimes.com/pages/travel/index.html">Travel</a>
                </li>

            
                <li class="shortcuts-E57A148E-0CB9-4C02-966D-28B119710151 ">
                    <a href="http://www.nytimes.com/pages/magazine/index.html">Magazine</a>
                </li>

            
                <li class="shortcuts-052C33AD-1404-4DB6-AA70-0901DB1AD95B ">
                    <a href="http://www.nytimes.com/section/t-magazine">T Magazine</a>
                </li>

            
                <li class="shortcuts-92720057-BCB6-4BDB-9351-12F29393259F ">
                    <a href="http://www.nytimes.com/pages/realestate/index.html">Real Estate</a>
                </li>

                        <li><button class="button all-sections-button">all</button></li>
    </ul>

</nav>
    <div class="search-flyout-panel flyout-panel">
    <button class="button close-button" type="button"><i class="icon"></i><span class="visually-hidden">Close search</span></button>
    <div class="ad">
        <div id="SponsorAd" class="sponsor-ad">
            <small class="ad-sponsor">search sponsored by</small>
        </div>
    </div>
    <nav class="search-form-control form-control layout-horizontal">
    <form class="search-form" role="search">
        <div class="control">
            <div class="label-container visually-hidden">
                                <label for="search-input">Search NYTimes.com</label>
                            </div>
            <div class="field-container">
                                <input id="search-input" name="search-input" type="text" class="search-input text" autocomplete="off" placeholder="Search NYTimes.com" />
                
                <button type="button" class="button clear-button" tabindex="-1" aria-describedby="clear-search-input"><i class="icon"></i><span id="clear-search-input" class="visually-hidden">Clear this text input</span></button>
                <div class="auto-suggest" style="display: none;">
                    <ol></ol>
                </div>
                <button class="button submit-button" type="submit">Go</button>
            </div>
        </div><!-- close control -->
    </form>
</nav>


</div><!-- close flyout-panel -->
    <div id="notification-modals" class="notification-modals"></div>

</header><!-- close masthead -->
<div id="masthead-placeholder" class="masthead-placeholder"></div>
                    <nav id="navigation" class="navigation">
    <h2 class="visually-hidden">Site Navigation</h2>
</nav> <!-- close navigation -->

<nav id="mobile-navigation" class="mobile-navigation hidden">
    <h2 class="visually-hidden">Site Mobile Navigation</h2>
</nav>

    <div id="navigation-edge" class="navigation-edge"></div>
    <div id="page" class="page">
                <main id="main" class="main" role="main">
                <div id="Top" class="ad hp-top-ad hidden nocontent robots-nocontent"></div>
<div id="Top_Close" class="ad hp-top-ad-close hidden nocontent robots-nocontent"></div>
<div id="Top5" class="ad top5-ad nocontent robots-nocontent"></div>

    <div class="span-abc-region region">
        <div class="collection">
            <!-- test 23 -->

<style>




/* Fix MM icons in kickers */
.kicker .icon:before { top: 0px; }
.kicker .media.slideshow { margin-bottom: 0px; }



/* Hiding Hacks */

.nythpHideKickers .kicker, .nythpHideBylines .byline, .nythpHideTimestamps .timestamp {
    display: none;
}

/* banner hed modifications */
.span-ab-top-region .story.theme-summary .story-heading {
  line-height: 2.1rem;
}


/* Alterations to the Centered Feature Photo Spot Treatment */

.b-column .photo-spot-region .story.theme-feature .story-heading {
    font-size: 1.35rem;
    line-height: 1.65rem;
}

.b-column  .photo-spot-region .story.theme-feature .story-heading {
    padding: 0 22px; /* for headline wrapping  */
}
.b-column .photo-spot-region .story.theme-feature .summary {
    line-height: 18px;
}

/* Breaking News/Developing Headers */
.nythpBreaking {
	color: #A81817;
	border-top: 3px solid #A81817;
	padding-top: 2px;
	padding-bottom: 3px;
        margin-top: 12px;
}

.nythpBreaking h6 {
	text-transform: uppercase;
	font-family: "nyt-franklin",arial,helvetica,sans-serif;
	font-weight: 700;
}

.nythpDeveloping {
	color: #FD8249;
	border-top-color: #FD8249;
}

.nythpBreaking.nythpNoRule {
	border: none;
        margin-top: 0px;
}

.above-banner-region .nythpBreaking {
margin-bottom: 10px;
}

/* Daypart Styles */

.pocket-region .story, .c-column #nythpDaypartRegion .story { margin-bottom: 15px !important; }

.pocket-region h4.sectionHeaderHome, .c-column #nythpDaypartRegion h4.sectionHeaderHome {
    font-size: 12px;
    line-height: 14px;
    font-weight: 700;
    font-family: "nyt-cheltenham-sh",georgia,"times new roman",times,serif;
    text-transform: uppercase;
    margin-bottom: 6px;
}

.pocket-region h5, .c-column #nythpDaypartRegion h5 {
	font-size: 14px;
	line-height: 16px;
	font-weight: 700;
	font-family: "nyt-cheltenham-sh",georgia,"times new roman",times,serif;
	margin-bottom: 2px;
}

.pocket-region .runaroundRight, .c-column #nythpDaypartRegion .runaroundRight {
	float: right;
	clear: right;
	margin: 3px 0px 6px 6px;
}

.pocket-region .summary, .c-column #nythpDaypartRegion .summary {
    font-size: 13px;
    line-height: 18px;
    font-weight: 400;
    font-family: georgia,"times new roman",times,serif;
    margin-bottom: 0px;
}

.pocket-region .refer li, .c-column #nythpDaypartRegion .refer li {
	background-image: url(http://css.nyt.com/images/icons/bullet4x4.gif);
	background-repeat: no-repeat;
	background-position: 0 .4em;
	padding-left: 8px;
	font-size: 12px;
	line-height: 14px;
	font-weight: 700;
	font-family: "nyt-cheltenham-sh",georgia,"times new roman",times,serif;
}



/* BEGIN .HPHEADER STYLING */

.wf-loading .hpHeader h6 {
    visibility: hidden;
  }

.hpHeader {
  margin-bottom: 8px;
}

.hpHeader h6 {
  font-family: "nyt-franklin",helvetica,arial,sans-serif;
  text-transform: uppercase;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  padding: 12px 4px 2px 0;
  border-bottom: 1px solid #999;
  border-top: 1px solid #E2E2E2;
}


.hpHeader h6 a, 
.hpHeader h6 a:visited  {
  text-decoration: none;
  color: #000;
}

.hpHeader h6:hover, .span-ab-top-region .hpHeader h6 a:hover, .top-news .b-column .hpHeader h6 a:hover, .b-column .split-layout .hpHeader h6:hover,  
.hpHeader h6:active, .span-ab-top-region .hpHeader h6 a:active, .top-news .b-column .hpHeader h6 a:active, .b-column .split-layout .hpHeader h6:active {
  border-bottom-color: #000;
}

/* B Column Centered Treatment */
.span-ab-top-region .hpHeader h6, .top-news .b-column .hpHeader h6  {
  text-align: center;
  border-bottom: none;
  padding: 0px;
}

.span-ab-top-region .hpHeader h6 a, .top-news .b-column .hpHeader h6 a  {
  display: inline-block;
  border-bottom: 1px solid #999;
  padding: 12px 4px 2px 4px;
}

/* Undo B Column Treatment for 3 Column Layouts and Split Code */
.b-column .split-layout .hpHeader h6 {
  text-align: left;    
  border-bottom: 1px solid #999;
  padding: 12px 4px 2px 0;
}

.b-column .split-layout .hpHeader h6 a {
  border-bottom: none;
  padding: 0;
}


/* Remove Top Rule When First in Region */
.collection:first-child .hpHeader h6, .collection:first-child .hpHeader h6 a {
  border-top: none;
  padding-top: 0;
}

/* Lens Header Styles */

.hpHeader h6, .span-ab-top-region .hpHeader h6 a, .top-news .b-column .hpHeader h6 a, .b-column .split-layout .hpHeader h6 { border-bottom-width: 2px; }

/* END .HPHEADER STYLING */


/* Briefing Newsletter */

.nythpBriefingNewsletterSignup {
	font-family: 'nyt-franklin', Arial, Helvetica, sans-serif;
	font-size: 11px;
	padding-left: 16px;
	background: url('http://graphics8.nytimes.com/packages/images/homepage/newsletter_icon.png') no-repeat;
	font-weight: 400;
}

a.nythpBriefingNewsletterSignup, a:link.nythpBriefingNewsletterSignup, a:visited.nythpBriefingNewsletterSignup {
	color: #326891;
}

</style>
<style>

.nythpBriefings h3.kicker {
    font-family: nyt-franklin,Arial,sans-serif;
    font-size: 12px;
    font-weight: 700;
    background: url('http://graphics8.nytimes.com/packages/images/homepage/briefings/dogear_sm.png') no-repeat scroll left top transparent;
    padding: 0 0 3px 20px;
    border-bottom: 1px solid #000;
    display: inline-block;
    color: #000;
    margin-bottom: 8px;
margin-top: 0px !important;
}

.nythpBriefings .timestamp {display: none;}

/* Gift Guide Promos */

.nythpGiftguide h3.kicker {

}

.nythpGiftguide article .kicker, .nythpGiftguide .byline {
	display: none;
}

.b-column .nythpGiftguide .image {
	margin-top: -40px;
}

.nythpGiftguide .theme-news-headlines li:before {
	background: none;
	border: none;
}

.nythpGiftguide .theme-news-headlines li {
	padding-left: 0px;
}

.nythpGiftguide .refer li .refer-heading {
	font-family: "nyt-franklin",arial,helvetica,sans-serif; 
	text-transform: uppercase; 
	font-size: 10px;
	font-weight: 400;
}

.nythpGiftguide .story.theme-summary .story-heading {
	font-size: 18px;
	line-height: 21px;
	font-weight: 700;
	font-family: "nyt-cheltenham",georgia,"times new roman",times,serif;
}

</style>

<script>
require(['foundation/main'], function () {
    require(['jquery/nyt', 'foundation/views/page-manager'], function ($, pageManager) {
        $(document).ready(function () {
             
              $("h3:contains('The Day Ahead')").parent().addClass("nythpBriefings");
              $("h3:contains('Holiday Gift Guide')").parent().addClass("nythpGiftguide");

        });
    });
});

</script></div>
    </div><!-- close span-abc-region -->

<div class="span-ab-layout layout">

    <div class="ab-column column">

        <section id="top-news" class="top-news">
            <h2 class="section-heading visually-hidden">Top News</h2>

            
            
            
            <div class="wide-b-layout layout">

                <div class="a-column column">

                    <div class="first-column-region region">

                        <div class="collection">
            <style type="text/css">

.nythpElection2016Header {

}

.nythpElection2016Header h6 {
    font-family: "nyt-franklin", arial, helvetica, sans-serif;
    text-transform: uppercase;
    font-size: 13px;
    font-weight: 700;
    background-image: url(http://graphics8.nytimes.com/newsgraphics/2015/02/25/election-navigation/assets/images/election-2016-logo.png);
    background-repeat: no-repeat;
    margin-bottom: 6px;
    height: 18px;
    background-position: left bottom;
    margin: 0 auto 6px;
    background-size: 18px 18px;
    padding: 5px 5px 0 25px;
    letter-spacing: 1px;
}

.nythpElection2016Header h6 a {
    text-decoration: none;
    color: #000;
}

 .nythpElection2016Header h6:hover,
 .nythpElection2016Header h6:active {
}

.nythpElection2016Header h6 a, 
.nythpElection2016Header h6 a:visited  {
    text-decoration: none;
    color: #000;
}

.nythpElection2016Header h6 em {
  color: #999;
  font-style: normal;
}

.span-ab-top-region .nythpElection2016Header, .b-column .nythpElection2016Header {
    text-align: center;
}

.span-ab-top-region .nythpElection2016Header h6, .b-column .nythpElection2016Header h6 {
    display: inline-block;

}

</style>

<div class="nythpHeader nythpElection2016Header">
  <h6><a href="http://www.nytimes.com/pages/politics/index.html">Election 2016</a></h6>
</div>

</div>
<div class="collection">
            <article class="story theme-summary lede" id="topnews-100000004085727" data-story-id="100000004085727" data-rank="0" data-collection-renderstyle="LedeSum">
    
        <h2 class="story-heading"><a href="http://www.nytimes.com/politics/first-draft/2015/12/10/trump-solidifies-his-lead-but-leaves-many-nervous/">Trump’s Lead Solidifies in Poll, but Many Are Nervous</a></h2>

            <p class="byline">By MEGAN THEE-BRENAN </p>
    
    <p class="summary">Donald J. Trump commands the support of 35 percent of Republican primary voters in a Times/CBS News poll taken largely before his latest comments about Muslims, but nearly two-thirds of American voters say his candidacy concerns or frightens them.</p>

	
	</article>


</div>
<div class="collection headlines">
            <ul class="theme-news-headlines">
                    <li>
            <div style="margin-top: -10px;"></div>            </li>
                    <li>
            <article class="story" id="topnews-100000004085917" data-story-id="100000004085917" data-rank="1" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/politics/first-draft/2015/12/10/ted-cruz-questions-donald-trumps-judgment-to-be-president/">In Private, Cruz Questions Trump’s ‘Judgment’ to Be President</a> <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="12:58 PM" data-utc-timestamp="1449770301">12:58 PM ET</time></h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004085794" data-story-id="100000004085794" data-rank="2" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/politics/first-draft/2015/12/10/donald-trump-puts-off-israel-trip-and-meeting-with-benjamin-netanyahu/">Trump Puts Off Netanyahu Meeting in Israel</a> <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="1:02 PM" data-utc-timestamp="1449770553">1:02 PM ET</time></h2>
</article>
            </li>
            </ul>
</div>
<hr class="single-rule"><div class="collection">
            <article class="story theme-summary" id="topnews-100000004086346" data-story-id="100000004086346" data-rank="0" data-collection-renderstyle="HpSum">
    
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/11/nyregion/connecticut-to-ban-gun-sales-to-those-on-federal-terrorism-lists.html">Connecticut to Ban Gun Sales to People on U.S. Terror Lists</a></h2>

            <p class="byline">By ELIZABETH A. HARRIS <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="3:00 PM" data-utc-timestamp="1449777643">3:00 PM ET</time></p>
    
    <p class="summary">Gov. Dannel P. Malloy announced the executive order on Thursday, saying that Connecticut would become the first state in the nation to enact such a measure.</p>

		    <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/11/nyregion/connecticut-to-ban-gun-sales-to-those-on-federal-terrorism-lists.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
	
	</article>

</div>
<div class="collection headlines">
            <ul class="theme-news-headlines">
                    <li>
            <div style="margin-top: -10px;"></div>            </li>
                    <li>
            <article class="story" id="topnews-100000004084340" data-story-id="100000004084340" data-rank="1" data-collection-renderstyle="HpBlogHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/us/how-to-stop-mass-shootings-in-america-times-readers-respond.html">How to Stop Mass Shootings: Times Readers Respond</a></h2>
</article>
            </li>
            </ul>
</div>
<hr class="single-rule"><div class="collection">
            <article class="story theme-summary" id="topnews-100000004084680" data-story-id="100000004084680" data-rank="0" data-collection-renderstyle="HpSum">
    
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/11/us/politics/pentagon-seeks-string-of-overseas-bases-to-contain-isis.html">As It Fights ISIS, Pentagon Seeks<br /> Bases Overseas</a></h2>

            <p class="byline">By MARK MAZZETTI and ERIC SCHMITT </p>
    
    <p class="summary">The plan proposed to the White House would build up a string of military bases in Africa, Southwest Asia and the Middle East that could be used to collect intelligence and carry out strikes against the Islamic State.</p>

		    <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/11/us/politics/pentagon-seeks-string-of-overseas-bases-to-contain-isis.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
	
	</article>

</div>
<hr class="single-rule"><div class="collection">
            <article class="story theme-summary" id="topnews-100000004085561" data-story-id="100000004085561" data-rank="0" data-collection-renderstyle="HpSum">
    
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/11/business/international/vw-emissions-scandal.html">VW Says Emissions Cheating Was Not a One-Time Error</a></h2>

            <p class="byline">By JACK EWING <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="2:39 PM" data-utc-timestamp="1449776347">2:39 PM ET</time></p>
    
    <p class="summary">Volkswagen said that its emissions cheating scandal began in 2005 with a decision to heavily promote diesel engines in the United States and a realization that they could not meet clean air standards.</p>

		    <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/11/business/international/vw-emissions-scandal.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
	
	</article>

</div>
                                                                        <hr class="single-rule"><div class="collection headlines">
                <h3 class="kicker collection-kicker">More News</h3>
        <ul class="theme-news-headlines">
                    <li>
            <article class="story" id="topnews-100000004084361" data-story-id="100000004084361" data-rank="0" data-collection-renderstyle="HpBlogHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/business/media/serial-season-2-bowe-bergdahl-recalls-his-afghan-odyssey.html">Focused on Bergdahl, New Season of ‘Serial’ Is Released</a></h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004085966" data-story-id="100000004085966" data-rank="1" data-collection-renderstyle="HpBlogHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/world/americas/argentina-president-mauricio-macri-sworn-in-snubbed-by-kirchner.html">Predecessor Snubs Argentine President’s Swearing-In</a></h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004084487" data-story-id="100000004084487" data-rank="2" data-collection-renderstyle="HpBlogHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/nyregion/dean-skelos-adam-skelos-corruption-trial.html">Jury Begins Deliberations in Trial of Skelos and Son</a></h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004085340" data-story-id="100000004085340" data-rank="3" data-collection-renderstyle="HpBlogHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/aponline/2015/12/10/world/middleeast/ap-ml-saudi-hajj.html">Hajj Stampede Killed Over 2,400, New Count Finds</a></h2>
</article>
            </li>
            </ul>
</div>

                    </div><!-- close first-column-region -->

                </div><!-- close a-column -->

                <div class="b-column column">

                                            <div class="photo-spot-region region">

                            <div class="collection">
            <article class="story theme-summary lede" id="topnews-100000004073476" data-story-id="100000004073476" data-rank="0" data-collection-renderstyle="LargeMediaHeadlineSum">
    
    <figure class="media photo">
    <div class="image">
        <a href="http://www.nytimes.com/2015/12/09/world/asia/a-new-frontier-for-the-american-west-in-the-far-east.html"><img src="http://static01.nyt.com/images/2015/12/07/world/07JACKSONHOLE1/07JACKSONHOLE1-largeHorizontal375.jpg" alt=""></a>
    </div>
    <figcaption class="caption" itemprop="description">
                    <span class="caption-text">A new section of housing being built in Jackson Hole in Hebei Province, on the outskirts of Beijing, mimics the aesthetics of Jackson Hole, Wyo.</span>
        
        	        <span class="credit" itemprop="copyrightHolder">
	            <span class="visually-hidden">Credit</span>
	            Sim Chi Yin for The New York Times	        </span>
            </figcaption>
</figure>

            <h3 class="kicker">Jackson Hole Journal </h3>
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/09/world/asia/a-new-frontier-for-the-american-west-in-the-far-east.html">Just Outside Beijing, a Little Bit of Wyoming</a></h2>

            <p class="byline">By ANDREW JACOBS </p>
    
    <p class="summary">On the outskirts of Beijing, more than 1,000 families have settled into a community modeled after an American frontier town, on streets with names like Aspen, Moose and Route 66.</p>

    
    </article>


</div>

                        </div><!-- close photo-spot-region -->

                        <hr class="scotch-rule" />
                    
                    
                        <div class="second-column-region region">

                            <div class="collection">
            <article class="story theme-summary" id="topnews-100000004018113" data-story-id="100000004018113" data-rank="0" data-collection-renderstyle="HpSumSmallMediaHigh">
    
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/11/us/psychiatric-drugs-are-being-prescribed-to-infants.html">Infants Given Psychiatric Drugs Without Testing</a></h2>

            <div class="thumb">
            <a href="http://www.nytimes.com/2015/12/11/us/psychiatric-drugs-are-being-prescribed-to-infants.html"><img src="http://static01.nyt.com/images/2015/12/09/us/10babymedsweb5/10babymedsweb5-thumbStandard-v2.jpg" alt=""></a>
        </div>
    
            <p class="byline">By ALAN SCHWARZ <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="11:03 AM" data-utc-timestamp="1449763420">11:03 AM ET</time></p>
    
    <p class="summary">
        Many doctors worry that these drugs are used despite no published research into their effectiveness and potential risks for children so young.    </p>

            <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/11/us/psychiatric-drugs-are-being-prescribed-to-infants.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
    
    </article>
</div>
<hr class="single-rule"><div class="collection">
            <article class="story theme-summary" id="topnews-100000004081715" data-story-id="100000004081715" data-rank="0" data-collection-renderstyle="HpSumSmallMedia">
    
    
    <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/11/nyregion/mayor-de-blasio-seeks-to-rebuild-momentum-for-affordable-housing-plan.html">Attacked on Affordable Housing, de Blasio Fights Back</a></h2>

            <p class="byline">By MICHAEL M. GRYNBAUM and MIREYA NAVARRO <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="10:41 AM" data-utc-timestamp="1449762085">10:41 AM ET</time></p>
    
            <div class="thumb">
            <a href="http://www.nytimes.com/2015/12/11/nyregion/mayor-de-blasio-seeks-to-rebuild-momentum-for-affordable-housing-plan.html"><img src="http://static01.nyt.com/images/2015/12/13/nyregion/REZONINGweb1/REZONINGweb1-thumbStandard.jpg" alt=""></a>
        </div>
    
    <p class="summary">
        A signature issue for Mayor Bill deBlasio has drawn opposition from the middle- and working-class communities it is meant to help.    </p>

            <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/11/nyregion/mayor-de-blasio-seeks-to-rebuild-momentum-for-affordable-housing-plan.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
    
    </article>

</div>
<hr class="single-rule"><div class="collection">
            <!--

======================================================

THIS IS A GENERATED TEMPLATE FILE. DO NOT EDIT.

======================================================

-->
<style>
/*"nyt-cheltenham", georgia, "times new roman", times, serif;*/

/**
 * @license
 * MyFonts Webfont Build ID 3134474, 2015-12-01T17:35:29-0500
 *
 * The fonts listed in this notice are subject to the End User License
 * Agreement(s) entered into by the website owner. All other parties are
 * explicitly restricted from using the Licensed Webfonts(s).
 *
 * You may obtain a valid license at the URLs below.
 *
 * Webfont: FuturaPT-Medium by ParaType
 * URL: http://www.myfonts.com/fonts/paratype/futura-book/futura-medium/
 *
 * Webfont: FuturaPT-Heavy by ParaType
 * URL: http://www.myfonts.com/fonts/paratype/futura-book/heavy/
 *
 *
 * License: http://www.myfonts.com/viewlicense?type=web&buildid=3134474
 * Licensed pageviews: 10,000
 * Webfonts copyright: Copyright &#x00A9; 1995 ParaGraph Intl., &#x00A9; 1998 ParaType Inc., ParaType Ltd. All rights reserved.
 *
 * © 2015 MyFonts Inc
*/


/* @import must be at top of file, otherwise CSS will not work */
@import url("//hello.myfonts.net/count/2fd40a");
@font-face {font-family: 'FuturaPT-Medium';src: url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_0_0.eot');src: url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_0_0.eot?#iefix') format('embedded-opentype'),url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_0_0.woff2') format('woff2'),url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_0_0.woff') format('woff'),url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_0_0.ttf') format('truetype');}
@font-face {font-family: 'FuturaPT-Heavy';src: url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_1_0.eot');src: url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_1_0.eot?#iefix') format('embedded-opentype'),url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_1_0.woff2') format('woff2'),url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_1_0.woff') format('woff'),url('http://graphics8.nytimes.com/newsgraphics/2015/12/08/gphp/7e86afc83344ad03a6ab534e29e8bea3a554026d/fonts/2FD40A_1_0.ttf') format('truetype');}
/*hp-bcol-photospot*/
.b-column .second-column-region .section-heading {
    font-family: "nyt-franklin", arial, helvetica, sans-serif;
    font-size: 14px;
    text-align: center;
    padding-bottom: 10px;
    margin-bottom: 0;
    max-width: none;
    background: white;
}
.b-column .second-column-region .section-heading a {
    text-decoration: none;
    border-bottom: 1px solid #aaa;
    display: inline-block;
}

.b-column .second-column-region #g-graphic {
    background: #000;
    display: block;
    margin: 0 auto;
    position: relative;
    width: 335px;
}
.b-column .second-column-region #g-graphic * {
    box-sizing: border-box;
}
.b-column .second-column-region #g-graphic img {
  height: auto;
  width: 100%;
}
.b-column .second-column-region .g-header {
    margin: auto;
    padding: 30px 20px;
    position: absolute;
    text-align: center;
    top: 50%;
    width: 100%;
    z-index: 99;
    -webkit-transform: translateY(-50%);
    -ms-transform: translateY(-50%);
    transform: translateY(-50%);
}
.b-column .second-column-region .g-headline,
.b-column .second-column-region .g-byline {
    color: #fff;
    display: block;
}
.b-column .second-column-region .g-headline {
    font-family: "FuturaPT-Medium", "nyt-franklin", arial, helvetica, sans-serif;
    font-size: 30px;
    font-weight: 200;
    letter-spacing: .18em;
    line-height: 1;
    opacity: 0.8;
    text-align: center;
    text-transform: uppercase;
}
.b-column .second-column-region .g-byline {
    display: block;
    font-family: georgia, "times new roman", times, serif;
    font-size: 13px;
    font-style: italic;
    line-height: 20px;
    opacity: 0.6;
    position: relative;
}
.b-column .second-column-region .g-byline:before {
    border-top: solid 1px #666;
    content: "";
    display: block;
    margin: 20px auto 0;
    padding-top: 20px;
    width: 200px;
}
.b-column .second-column-region .g-byline span {
    font-family: FuturaPT-Medium;
    font-style: normal;
    letter-spacing: 1px;
    text-transform: uppercase;
}
.b-column .second-column-region .g-summary {
/*    border-bottom: 1px solid #e2e2e2;*/
    margin: 10px auto;
/*    padding-bottom: 10px;*/
    width: 335px;
}
</style>

<h2 class="section-heading">
  <a href="http://www.nytimes.com/section/magazine">From the Magazine</a>
</h2>
<a href="http://www.nytimes.com/interactive/2015/12/10/magazine/great-performers-take-flight.html" id="g-graphic">
    <img src="http://graphics8.nytimes.com/images/2015/12/13/magazine/13performers-take-flight-slide-2PXH/13performers-take-flight-slide-2PXH-videoHpMedium.jpg">
    <div class="g-header">
        <div class="g-headline">Take Flight</div>
        <div class="g-byline">Directed by <span>Daniel Askill</span></div>
    </div>
</a>
<p class="summary g-summary">The year’s best actors lift off in a series of tributes to the ultimate Hollywood magic trick.</p>
<!-- Pipeline: 2015-12-08-gphp December 10, 2015, 09:13PM 7e86afc83344ad03a6ab534e29e8bea3a554026d --><style>
#vr-promo{
  height:65px;
  padding: 0 20px;
  margin:12px auto 9px auto;
  text-align: left;
  display: block;
}
/*
#vr-promo:before{
  content:"";
  display:block;
  width:100px:
  height:1px;
  border-top:1px solid #efefef;
  margin:0 auto 12px auto;
}
*/
#vr-promo:hover {
  text-decoration: none;
}

#vr-promo .thumb{
  width:50px;
  height:50px;
  display: inline-block;
  vertical-align: middle;
  margin-left: 0;
}

#vr-promo .thumb img{
  width:100%;
  border-radius: 4px;
}

#vr-promo p.summary{
    display: inline-block;
    vertical-align: middle;
    width: calc(100% - 130px);
    line-height: 1.4;
    margin-bottom: 0;
    margin-left: 10px;
    text-align: left;
    color: black;
    font-weight: bold;
    font-family: "nyt-cheltenham-sh",georgia,"times new roman",times,serif;
}

#vr-promo:hover .summary span{
  text-decoration: underline;
}

</style>
<a id="vr-promo" href="http://www.nytimes.com/newsgraphics/2015/nytvr/">
  <div class="thumb">
    <img src="http://graphics8.nytimes.com/images/2015/11/06/magazine/magvr-thumb/magvr-thumb-master180-v3.png" />
  </div>
  <p class="summary"><span>Experience what it's like to fly among the (Hollywood) stars. Try our new virtual reality app.</span></p>
</a></div>
<hr class="single-rule"><div class="collection">
            <article class="story theme-summary" id="topnews-100000004069834" data-story-id="100000004069834" data-rank="0" data-collection-renderstyle="HpSumMediumMediaHigh">
    
            <figure class="media photo medium-thumb">
    <div class="image">
        <a href="http://www.nytimes.com/2015/12/13/movies/best-movies-2015.html"><img src="http://static01.nyt.com/images/2015/12/13/movies/13BESTMOVIES/13BESTMOVIES-mediumFlexible177-v3.jpg" alt="" /></a>
    </div>
</figure>
    
    
    <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/13/movies/best-movies-2015.html">The Best Movies of 2015</a></h2>

    
    <p class="summary">
        The film critics of The New York Times — Manohla Dargis, A. O. Scott and Stephen Holden — share their picks for the best movies of the year.    </p>

    
        <ul class="refer theme-news-headlines">
            <li>
            <article class="story" id="topnews-100000004069834">
                <h2 class="refer-heading"><a href="http://www.nytimes.com/2015/12/11/movies/golden-globes-2016-nominations.html">‘Carol’ and ‘Big Short’ Are Top Golden Globe Nominees</a></h2>
            </article>
        </li>
        </ul>

</article>
</div>
<div class="collection">
            <style type="text/css">

.wf-loading .hpHeader h6 {
    visibility: hidden;
  }

.hpHeader {
  margin-bottom: 18px;
}

.hpHeader h6 {
  font-family: "nyt-franklin",helvetica,arial,sans-serif;
   text-transform: uppercase;
   font-size: 12px;
   line-height:12px;
   font-weight: 700;
  letter-spacing: .5px;
  padding: 12px 4px 0px 0;
  border-top: 1px solid #E2E2E2;
}

.hpHeader.nythpPCTHeader h6 {
border-bottom: none;
}

.hpHeader h6 a, 
.hpHeader h6 a:visited  {
  text-decoration: none;
  color: #000;
  border-bottom: 2px solid #999;
  padding: 0 4px 0 0;
}

.hpHeader h6:hover, .span-ab-top-region .hpHeader h6 a:hover, .above-banner-region .hpHeader h6 a:hover, .top-news .b-column .hpHeader h6 a:hover, .b-column .split-layout .hpHeader h6:hover,  
.hpHeader h6:active, .span-ab-top-region .hpHeader h6 a:active, .above-banner-region .hpHeader h6 a:active, .top-news .b-column .hpHeader h6 a:active, .b-column .split-layout .hpHeader h6:active {
  border-bottom-color: #999;
}


/* B Column Centered Treatment */
.span-ab-top-region .hpHeader h6, .above-banner-region .hpHeader h6, .top-news .b-column .hpHeader h6  {
  text-align: center;
  border-bottom: none;
  padding: 0px;
}

.span-ab-top-region .hpHeader h6 a, .above-banner-region .hpHeader h6 a, .top-news .b-column .hpHeader h6 a  {
  display: inline-block;
  border-bottom: 2px solid #999;
  padding: 12px 0px 2px 0px;
}

/* Undo B Column Treatment for 3 Column Layouts and Split Code */
.b-column .split-layout .hpHeader h6 {
  text-align: left;    
  border-bottom: 2px solid #999;
  padding: 12px 4px 2px 0;
}

.b-column .split-layout .hpHeader h6 a {
  border-bottom: none;
  padding: 0;
}


/* Remove Top Rule When First in Region */
.collection:first-child .hpHeader h6, .collection:first-child .hpHeader h6 a {
  border-top: none;
  padding-top: 0;
}


/* Header Styles */
.nythpPCTHeader h6, .nythpPCTHeader h6 a, .nythpPCTHeader h6 a {
  border-bottom-width: 2px;

}

.nythpPCTHeader h6 em {
  color: #999;
  font-style: normal;
}
.nythpPCTHeader:hover h6 a{
  border-color:#000!important;
}


</style>


<div class="hpHeader nythpPCTHeader">
  <h6><a href="http://www.nytimes.com/news-event/un-climate-change-conference">Paris <em>Climate Talks</em></a></h6>
</div>
</div>
<div class="collection headlines">
            <ul class="theme-news-headlines">
                    <li>
            <article class="story" id="topnews-100000004084481" data-story-id="100000004084481" data-rank="0" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/world/delegates-at-climate-talks-focus-on-saving-the-worlds-forests.html">Finding a Few Billion Dollars to Save the Forests</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004083453" data-story-id="100000004083453" data-rank="1" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/10/us/politics/kerry-in-familiar-spot-trying-to-coax-deal-in-meetings-closing-days.html">Kerry Finds Himself in Last-Minute Negotiations Again</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004085145" data-story-id="100000004085145" data-rank="2" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/world/asia/china-smog-face-mask.html">Amid China’s Smog Worries: Counterfeit Masks</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004084752" data-story-id="100000004084752" data-rank="3" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/interactive/2015/12/10/science/paris-climate-change-talks-quiz.html">Quiz: Eight Questions About Climate Change and Paris</a> </h2>
</article>
            </li>
            </ul>
</div>

                        </div><!-- close second-column-region -->

                    
                </div><!-- close b-column -->

            </div><!-- close wide-b-layout -->

            
                            <div class="span-ab-bottom-region region">
                    <hr class="scotch-rule" />
<div class="split-3-layout layout theme-base">
<h2 class="section-heading">
</h2>
<div class="column">
    <article class="story theme-summary " id="topnews-100000004079388" data-story-id="100000004079388" data-rank="0" data-collection-renderstyle="HPMediumMediaHedSumDaypart">
                <a href="http://www.nytimes.com/2015/12/13/fashion/the-heart-said-yes-the-hindu-horoscope-said-no.html">
                        <div class="wide-thumb">
                    <img src="http://static01.nyt.com/images/2015/12/13/fashion/13MODERN/13MODERN-mediumThreeByTwo210.jpg" />
                                    </div>
                 </a>
        <h2 class="story-heading">
            <a href="http://www.nytimes.com/2015/12/13/fashion/the-heart-said-yes-the-hindu-horoscope-said-no.html">Modern Love: Heart Said Yes; Horoscope Said No</a>
        </h2>
        <p class="summary">
            Was my fate truly predetermined? The astrologers in India seemed to know pretty well.        </p>
    </article>
</div>
<div class="column">
    <article class="story theme-summary " id="topnews-100000004084086" data-story-id="100000004084086" data-rank="1" data-collection-renderstyle="HPMediumMediaHedSumDaypart">
                <a href="http://www.nytimes.com/2015/12/11/business/a-middle-ground-between-contract-worker-and-employee.html">
                        <div class="wide-thumb">
                    <img src="http://static01.nyt.com/images/2015/12/11/business/11labor-web1/11labor-web1-mediumThreeByTwo210.jpg" />
                                    </div>
                 </a>
        <h2 class="story-heading">
            <a href="http://www.nytimes.com/2015/12/11/business/a-middle-ground-between-contract-worker-and-employee.html">More Than a Contractor, Not Quite an Employee</a>
        </h2>
        <p class="summary">
            In the so-called gig economy, ambiguity in the status of workers at companies like Uber and Lyft has led to calls for the creation of a new kind of employment status.        </p>
    </article>
</div>
<div class="column">
    <article class="story theme-summary " id="topnews-100000004078878" data-story-id="100000004078878" data-rank="2" data-collection-renderstyle="HPMediumMediaHedSumDaypart">
                <a href="http://www.nytimes.com/2015/12/10/fashion/schooling-children-in-manners-thank-you-very-much.html">
                        <div class="wide-thumb">
                    <img src="http://static01.nyt.com/images/2015/12/10/fashion/10CHIVALRY1-DIPTYCH/10CHIVALRY1-DIPTYCH-mediumThreeByTwo210.jpg" />
                                    </div>
                 </a>
        <h2 class="story-heading">
            <a href="http://www.nytimes.com/2015/12/10/fashion/schooling-children-in-manners-thank-you-very-much.html">Chivalry Isn’t Dead — <br /> It Just Got Smaller</a>
        </h2>
        <p class="summary">
            Parents, teachers and entrepreneurs are teaching young people old-fashioned civility and manners.        </p>
    </article>
</div>
</div>
                </div><!-- close span-ab-bottom-region -->
            
        </section><!-- close top-news -->

    </div><!-- close ab-column -->

    <div class="c-column column">

        <div id="Middle" class="ad middle-ad hidden nocontent robots-nocontent"></div>

        <div class="region c-column-top-span-region">

            
        </div><!-- close c-column-top-span-region -->

        <section class="opinion">

            <div class="region opinion-c-col-top-region">
                <div class="collection">
            <section class="opinion">
  <h2 class="section-heading"><a href="http://www.nytimes.com/pages/opinion/index.html">The Opinion Pages</a>
  </h2>
</section></div>
            </div> <!-- close opinion-c-col-top-region -->

            <div class="layout split-layout">

                <div class="column">
                    <div class="region opinion-c-col-left-region">
                                            <div class="collection">
            <article class="story theme-summary" id="topnews-100000004083335" data-story-id="100000004083335" data-rank="0" data-collection-renderstyle="HpSum">
    
            <h3 class="kicker">Op-Ed | Linda Greenhouse </h3>
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/10/opinion/guns-and-thunder-on-the-supreme-courts-right.html">Guns and Thunder on the Supreme Court’s Right</a></h2>

    
    <p class="summary">The court’s decision not to accept a gun case highlights a chasm on its conservative flank.</p>

		    <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/10/opinion/guns-and-thunder-on-the-supreme-courts-right.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
	
	</article>

</div>
                                            <div class="collection headlines">
            <ul class="theme-news-headlines">
                    <li>
            <article class="story" id="topnews-100000004084253" data-story-id="100000004084253" data-rank="0" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/10/opinion/a-crass-act.html">Blow: A Crass Act</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004072211" data-story-id="100000004072211" data-rank="1" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/10/opinion/the-evil-that-cannot-be-left-unanswered.html">Cohen: The Evil That Cannot Be Left Unanswered</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004084301" data-story-id="100000004084301" data-rank="2" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/10/opinion/republicans-guns-and-abortion.html">Collins: Republicans, Guns and Abortion</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004083933" data-story-id="100000004083933" data-rank="3" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/10/opinion/campaign-stops/the-crisis-of-republican-authority.html">Douthat: The Crisis of Republican Authority</a> <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="3:00 PM" data-utc-timestamp="1449777603">3:00 PM ET</time></h2>
</article>
            </li>
            </ul>
</div>
                    </div>
                </div>

                <div class="column">
                    <div class="region opinion-c-col-right-region">
                                            <div class="collection">
            <article class="story theme-summary" id="topnews-100000004084148" data-story-id="100000004084148" data-rank="0" data-collection-renderstyle="HpSumXSMedia">
    
            <h3 class="kicker">Op-Ed Contributor </h3>
        <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/10/opinion/we-can-regulate-guns-at-the-local-level-t.html">We Can Regulate Guns at the Local Level, Too</a></h2>

            <div class="small-thumb">
            <a href="http://www.nytimes.com/2015/12/10/opinion/we-can-regulate-guns-at-the-local-level-t.html"><img src="http://static01.nyt.com/images/2015/12/11/opinion/11blocher/11blocher-blogSmallThumb.jpg" alt=""></a>
        </div>
    
            <p class="byline">By JOSEPH BLOCHER </p>
    
    <p class="summary">
        States should not wait to pass reasonable laws that fit their own circumstances.    </p>

            <p class="theme-comments">
    <a href="http://www.nytimes.com/2015/12/10/opinion/we-can-regulate-guns-at-the-local-level-t.html?hp&amp;target=comments#commentsContainer" class="comments-link"><i class="icon sprite-icon comments-icon"></i><span class="comment-count">&nbsp;Comments</span></a>
</p>
    
    </article>

</div>
                                            <div class="collection headlines">
                <h3 class="kicker collection-kicker">More in Opinion</h3>
        <ul class="theme-news-headlines">
                    <li>
            <article class="story" id="topnews-100000004085439" data-story-id="100000004085439" data-rank="0" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://opinionator.blogs.nytimes.com/2015/12/10/bell-hooks-buddhism-the-beats-and-loving-blackness/">bell hooks: Buddhism, the Beats and Loving Blackness</a> <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="10:52 AM" data-utc-timestamp="1449762767">10:52 AM ET</time></h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004081144" data-story-id="100000004081144" data-rank="1" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/2015/12/11/opinion/let-israel-fight-isis.html">Rosner: Let Israel Fight ISIS</a> </h2>
</article>
            </li>
                    <li>
            <article class="story" id="topnews-100000004086033" data-story-id="100000004086033" data-rank="2" data-collection-renderstyle="HpHeadline">
        <h2 class="story-heading"><i class="icon"></i><a href="http://www.nytimes.com/roomfordebate/2015/12/10/trade-an-end-to-sanctions-for-putins-help-against-isis">Room for Debate: Bargaining Over Russian Sanctions</a> <time class="timestamp" datetime="2015-12-10" data-eastern-timestamp="11:27 AM" data-utc-timestamp="1449764874">11:27 AM ET</time></h2>
</article>
            </li>
            </ul>
</div>
                    </div>
                </div>

            </div> <!-- close split-layout -->

            <div class="region opinion-c-col-bottom-region">
                <div class="collection">
            <style>	


.c-column.column section.opinion div time.timestamp{
	display:none;
}


</style><style>	
.c-column.column section.opinion div p.theme-comments{
	display:none;
}



</style></div>
            </div> <!-- close opinion-c-col-bottom-region -->

        </section> <!-- close opinion -->

        <section class="user-subscriptions hidden">

            <h2 class="section-heading visually-hidden">User Subscriptions</h2>

            <div class="collection insider-collection">
  <div id="times-insider-subscription" class="times-insider-subscription hidden">
    <ul class="theme-news-headlines">
                              <li>
              <article class="story">
                <h2 class="story-heading">
                  <a href="http://www.nytimes.com/2015/12/03/insider/1926-draw-she-said.html">1926 | Draw, She Said</a>
                </h2>
              </article>
            </li>
                      <li>
              <article class="story">
                <h2 class="story-heading">
                  <a href="http://www.nytimes.com/2015/12/08/insider/new-critic-at-large-breathtakingly-funny-absolutely-serious.html">New Critic at Large: ‘Breathtakingly Funny, Absolutely Serious’</a>
                </h2>
              </article>
            </li>
                      <li>
              <article class="story">
                <h2 class="story-heading">
                  <a href="http://www.nytimes.com/2015/12/07/insider/the-gun-epidemic-the-making-of-a-page-1-editorial.html">The Gun Epidemic: The Making of a Page 1 Editorial</a>
                </h2>
              </article>
            </li>
                      </ul>
  <div class="thumb">
        <svg class="insider-logo" width="65" height="20" role="img" aria-label="Insider">
          <image width="65" height="20" xlink:href="http://a1.nyt.com/assets/homepage/20151207-151834/images/foundation/logos/insider-logo-240x72.svg" src="http://a1.nyt.com/assets/homepage/20151207-151834/images/foundation/logos/insider-logo-240x72.png" alt="Insider" />
      </svg>
    </div>
</div><!--close times-insider-subscription --></div><!-- close collection -->

<div class="collection">
  <div id="times-premier-crossword-subscription" class="times-premier-crossword-subscription hidden">
      <div class="layout split-layout">
        <div class="column">
          <div class="collection insider-collection">
            <article class="story">
              <h3 class="kicker">
                <a href="http://www.nytimes.com/section/insider">Times Insider &raquo;</a>
              </h3>
                              <h2 class="story-heading">
                  <a href="http://www.nytimes.com/2015/12/03/insider/1926-draw-she-said.html">1926 | Draw, She Said</a>
                </h2>
                          </article>
          </div>
        </div><!-- close column -->
        <div class="column">
	<div class="collection crosswords-collection">
	    <article class="story">
	    	<h3 class="kicker">
	    		<a href="http://www.nytimes.com/crosswords">The Crossword &raquo;</a>
	    	</h3>
			<h2 class="story-heading">
				<a href="http://www.nytimes.com/crosswords">Play Today&rsquo;s Puzzle </a>
			</h2>
			<div class="thumb">
				<a href="http://www.nytimes.com/crosswords">
					<img src="http://static01.nyt.com/images/crosswords/crosswords_30x30.png" alt="" />
				</a>
			</div>
	    </article>
	</div>
</div>
      </div><!--close TimesPremiercrossword -->
    </div><!--close times-premier-crossword-subscription -->
</div><!-- close collection -->

<div class="collection">
    <div id="crossword-subscription" class="crossword-subscription hidden">
        <div class="layout split-layout">
            <div class="column">
	<div class="collection crosswords-collection">
	    <article class="story">
	    	<h3 class="kicker">
	    		<a href="http://www.nytimes.com/crosswords">The Crossword &raquo;</a>
	    	</h3>
			<h2 class="story-heading">
				<a href="http://www.nytimes.com/crosswords">Play Today&rsquo;s Puzzle </a>
			</h2>
			<div class="thumb">
				<a href="http://www.nytimes.com/crosswords">
					<img src="http://static01.nyt.com/images/crosswords/crosswords_30x30.png" alt="" />
				</a>
			</div>
	    </article>
	</div>
</div>
            <div class="column">
                <div class="collection crosswords-collection">
                    <article class="story">
                        <h3 class="kicker">
                          <a href="http://wordplay.blogs.nytimes.com">Wordplay &raquo;</a>
                        </h3>
                                                  <h2 class="story-heading">
                            <a href="http://wordplay.blogs.nytimes.com/2015/12/09/obstacles/">Obstacles</a>
                          </h2>
                                            </article>
                </div>
            </div><!-- close column -->
        </div><!-- close layout -->
    </div><!-- close crossword-subscription -->
</div><!--close collection -->

        </section><!-- close user-subscriptions -->

        <div id="HPMiddle" class="ad hpmiddle-ad nocontent robots-nocontent"></div>

        <div class="region c-column-middle-span-region">

            <div class="collection">
            <link rel="stylesheet" type="text/css" href="http://int.nyt.com/applications/portals/assets/portal-3dc2bf0e7593a689f857e1edec4a2566.css"/>

<div id="nytint-hp-watching">
  <div class="portal-container">
    <header class="portal-header">
      <h4>Watching</h4>
    </header>
    <div class="portal-posts-frame expanded"></div>
    <footer class="portal-footer"></footer>
  </div>
</div>

<script type="text/javascript" src="http://int.nyt.com/applications/portals/assets/portal/app-d37d526acc5f1ea70c6a0bee273416cc.js"></script>
<script type="text/javascript">
require(['foundation/main'], function() {
  require(['homepage/main'], function() {
    require(['portal/app'], function(Portal) {
        
      var opts = {
        env: 'production_published',
        matchHeight: {
          match: '.span-ab-layout.layout > .ab-column',
          container: '.c-column.column',
          maxHeight: 2000
        }
      }

      var force = window.location.search.indexOf('portal_variant=watchingNoScroll') !== -1;
      if (force || (NYTABTEST && NYTABTEST.engine &&
          NYTABTEST.engine.isUserVariant('watchingNoScroll') === '1')) {
        opts.variation = 'simple';
        opts.poll = false;
        opts.limit = 20;
      }

      var watching = Portal.create('#nytint-hp-watching', opts);
    });
  });
});
</script>

<!-- HpPrototype: WatchingEnabled: DO NOT REMOVE --></div>

        </div><!-- close c-column-middle-span-region -->

        <div class="region c-column-above-moth-fixed-region">

            
        </div><!-- close c-column-above-moth-fixed-region -->

        <div class="region c-column-above-moth-region">

            
        </div><!-- close c-column-above-moth-region -->

    </div><!-- close c-column -->

</div><!-- close span-ab-layout -->


<section id="paid-post-five-pack" class="paid-post-five-pack hidden nocontent robots-nocontent">
<h2 class="section-heading">From Our Advertisers</h2>
    <ul class="story-menu">
        <li id="PaidPostFivePack1" class="story-menu-item ad"></li>
        <li id="PaidPostFivePack2" class="story-menu-item ad"></li>
        <li id="PaidPostFivePack3" class="story-menu-item ad"></li>
        <li id="PaidPostFivePack4" class="story-menu-item ad"></li>
        <li id="PaidPostFivePack5" class="story-menu-item ad"></li>
    </ul>
</section>
<div id="HPBreak" class="ad hpbreak-ad nocontent robots-nocontent hidden"></div>

<div id="video-player-region" class="video-player-region region">
    <div class="collection">
            <div id="video-module-loader" class="loader-container video-section-loader-container">
    <div class="loader loader-t-logo-32x32-ecedeb-ffffff"><span class="visually-hidden">Loading...</span></div>
</div>
<section id="video-section"
         class="video-section invisible"
         data-playlist-id="1194811622188"
         data-api-token="cE97ArV7TzqBzkmeRVVhJ8O6GWME2iG_bRvjBTlNb4o."
         data-player-id="2640832222001"
         data-publisher-id="1749339200">
    <header class="section-header">
        <h2 class="section-heading"><a class="times-video-link" href="http://www.nytimes.com/video">times<span>video</span></a></h2>
        <a href="http://www.nytimes.com/video?src=vidm" class="user-action explore-all-videos-link"> explore all videos &raquo;</a>
    </header>
    <div class="column a-column">
        <article class="story">
            <figure class="media video video-player" aria-label="media" role="group">
                <span class="visually-hidden">Video Player</span>
                <img src="" class="poster" alt="" />
                <div class="video-player-container"></div>
                <div class="media-action-overlay"></div>
                <div class="sharetools video-sharetools"
                    data-shares="email|,facebook|,twitter|,embed|,show-all|"
                    data-url=""
                    data-title=""
                    data-description=""
                    data-content-type="video">
                </div>
                <figcaption class="credit"></figcaption>
            </figure>
        </article>
    </div>
    <div class="column b-column">
        <div id="video-playlist-container" class="video-playlist-container">
            <ul id="video-playlist" class="video-playlist"></ul>
        </div>
    </div> <!-- end b-column -->
</section>
</div>
</div><!-- close video-player-region -->

<section class="inside-nyt">
    <div class="inside-nyt-region region">
        <h2 class="section-heading">Inside Nytimes.com</h2>
        <div id="inside-nyt-browser" class="inside-nyt-browser">
            <div class="navigation-control">
                <button class="button previous deactivated">
                    <div class="arrow arrow-left">
                    <span class="visually-hidden">Go to the previous story</span>
                    <div class="arrow-conceal"></div>
                    </div>
                </button>
                <button class="button next">
                    <div class="arrow arrow-right">
                    <span class="visually-hidden">Go to the next story</span>
                    <div class="arrow-conceal"></div>
                    </div>
                </button>
            </div>
            <div class="carousel">
                <ol class="menu layout-horizontal theme-story">
                    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/fashion/index.html">Fashion & Style</a></h2>

    <article class="story theme-summary" data-story-id="100000004083571" data-rank="0" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/10/fashion/derek-jeter-on-his-second-act-as-a-sports-media-mogul.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/fashion/10JETER1/10JETER1-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">Derek Jeter on His Second Act</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/opinion/index.html">Opinion</a></h2>

    <article class="story theme-summary" data-story-id="100000004083390" data-rank="1" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/10/opinion/why-the-new-education-law-is-good-for-children-left-behind.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/opinion/10kirp/10kirp-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">Op-Ed: The New Law for Children Left Behind</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/dining/index.html">Food</a></h2>

    <article class="story theme-summary" data-story-id="100000004075454" data-rank="2" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/16/dining/best-wines-2015.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/16/dining/16TOPPOUR1/16TOPPOUR1-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">The Pour: The Top 10 Wines of 2015</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/upshot">The Upshot</a></h2>

    <article class="story theme-summary" data-story-id="100000004085261" data-rank="3" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/interactive/2015/12/09/upshot/top-stories.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/09/upshot/09top-stories-promo2/09top-stories-promo2-mediumSquare149-v3.png" alt="">
            </div>
            <h2 class="story-heading">The Stories That Held You the Longest</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/opinion/index.html">Opinion</a></h2>

    <article class="story theme-summary" data-story-id="100000004081339" data-rank="4" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/09/opinion/diversity-makes-you-brighter.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/09/opinion/09starklevine/09starklevine-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">Op-Ed: Diversity Makes You Brighter</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/books/review/index.html">Sunday Book Review</a></h2>

    <article class="story theme-summary" data-story-id="100000004071237" data-rank="5" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/13/books/review/the-white-road-by-edmund-de-waal.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/books/review/13MILLER/13MILLER-mediumSquare149-v2.jpg" alt="">
            </div>
            <h2 class="story-heading">‘The White Road,’ by Edmund de Waal</h2>
        </a>
    </article>

        </section>
    </li>

    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/world/middleeast/index.html">Middle East</a></h2>

    <article class="story theme-summary" data-story-id="100000004083498" data-rank="0" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/10/world/middleeast/fatema-mernissi-a-founder-of-islamic-feminism-dies-at-75.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/world/10mernissi-obit-web/10mernissi-obit-web-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">Fatema Mernissi Dies; Studied Feminist Islam</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/sports/baseball/index.html">Baseball</a></h2>

    <article class="story theme-summary" data-story-id="100000004084091" data-rank="1" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/10/sports/baseball/yoenis-cespedes-the-key-for-new-york-mets.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/sports/10POWELLweb1/10POWELLweb1-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">Ben Zobrist Was Never the Guy for the Mets</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/opinion/index.html">Opinion</a></h2>

    <article class="story theme-summary no-thumb" data-story-id="100000004085601" data-rank="2" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://opinionator.blogs.nytimes.com/2015/12/10/bell-hooks-buddhism-the-beats-and-loving-blackness/">
            <h2 class="story-heading">bell hooks: Buddhism, the Beats and Loving Blackness</h2>
            <p class="summary">Feminism does not ground me. It is the discipline that comes from spiritual practice that is the foundation of my life.</p>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/arts/music/index.html">Music</a></h2>

    <article class="story theme-summary" data-story-id="100000004081533" data-rank="3" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/13/arts/music/best-albums-of-2015.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/arts/music/13BESTPOP/13BESTPOP-mediumSquare149-v2.jpg" alt="">
            </div>
            <h2 class="story-heading">The Best Albums of 2015</h2>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/opinion/index.html">Opinion</a></h2>

    <article class="story theme-summary no-thumb" data-story-id="100000004085622" data-rank="4" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/10/opinion/trumps-anti-muslim-plan-is-awful-and-constitutional.html">
            <h2 class="story-heading">Op-Ed: Trump’s Plan Is Awful. And Constitutional.</h2>
            <p class="summary">The Supreme Court would have to approve his proposal to bar Muslim immigrants. But that doesn’t mean Americans should accept it.</p>
        </a>
    </article>

        </section>
    </li>
    <li>
        <section>
        <h2 class="section-heading"><a href="http://www.nytimes.com/pages/arts/television/index.html">Television</a></h2>

    <article class="story theme-summary" data-story-id="100000004081998" data-rank="5" data-collection-renderstyle="Moth">
        <a class="story-link" href="http://www.nytimes.com/2015/12/10/arts/television/christmas-shows-are-tvs-december-comfort-food.html">
            <div class="wide-thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/arts/10HOLIDAYSHOWS1/10HOLIDAYSHOWS1-mediumSquare149.jpg" alt="">
            </div>
            <h2 class="story-heading">Christmas TV Shows Are December Comfort Food</h2>
        </a>
    </article>

        </section>
    </li>

                </ol>
            </div>
        </div>
    </div>
</section>

<div id="HPMidLeader" class="ad hpmidleader-ad nocontent robots-nocontent"></div>

<div class="span-ab-layout layout">

    <div class="ab-column column">

        <hr class="scotch-rule" />

            <section class="well">
    <div class="region well-region">
        <h2 class="section-heading visually-hidden">Sections</h2>

                
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/world/index.html">World &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004065069" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/10/world/africa/they-helped-erase-ebola-in-liberia-now-liberia-is-erasing-them.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/07/world/07LIBERIA2/07LIBERIA2-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Marshall Journal: They Helped Erase Ebola in Liberia. Now Liberia Is Erasing Them.        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085759" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/world/asia/afghanistan-spy-chief-resigns.html">
            Afghanistan and Pakistan Agree to Reopen Talks With an Absent Taliban        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004086216" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/world/asia/north-korea-kim-hydrogen-bomb.html">
            North Korean Leader’s Claim of Hydrogen Bomb Draws Skepticism        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/business/index.html">Business Day &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004084361" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/business/media/serial-season-2-bowe-bergdahl-recalls-his-afghan-odyssey.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/arts/SERIAL2/SERIAL2-thumbStandard-v5.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        ‘Serial’ Season 2 Focuses on Bowe Bergdahl’s Story        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085818" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/business/daily-stock-market-activity.html">
            Recovering Energy Shares Help Market End a Skid        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004086539" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/business/fiat-chrysler-is-fined-and-agrees-to-fix-safety-reporting-system.html">
            Fiat Chrysler Is Fined and Agrees to Fix Safety Reporting System        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/opinion/index.html">Opinion &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004083390" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/10/opinion/why-the-new-education-law-is-good-for-children-left-behind.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/opinion/10kirp/10kirp-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Op-Ed Contributor: Why the New Education Law Is Good for Children Left Behind        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084271" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/opinion/the-trump-effect-and-how-it-spreads.html">
            Editorial: The Trump Effect, and How It Spreads        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084253" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/opinion/a-crass-act.html">
            Charles M. Blow: A Crass Act        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/national/index.html">U.S. &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004084340" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/us/how-to-stop-mass-shootings-in-america-times-readers-respond.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/us/11shooting-callout1/11shooting-callout1-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        How to Stop Mass Shootings in America: Times Readers Respond        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004018113" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/us/psychiatric-drugs-are-being-prescribed-to-infants.html">
            Psychiatric Drugs Are Being Prescribed to Infants        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084680" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/us/politics/pentagon-seeks-string-of-overseas-bases-to-contain-isis.html">
            As It Fights ISIS, Pentagon Seeks String of Bases Overseas        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/technology/index.html">Technology &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004084689" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/science/an-advance-in-artificial-intelligence-rivals-human-vision-abilities.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/11/business/11vision-web1/11vision-web1-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        An Advance in Artificial Intelligence Rivals Human Abilities        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083598" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/business/dealbook/marissa-mayer-has-a-year-to-fulfill-yahoos-potential.html">
            Deal Professor: Marissa Mayer Has a Year to Fulfill Yahoo’s Potential        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084295" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/business/dealbook/how-to-value-yahoos-core-business.html">
            How to Value Yahoo’s Core Business        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/arts/index.html">Arts &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004075803" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/movies/golden-globes-2016-nominations.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/11/arts/11carol1_hp/11carol1_hp-thumbStandard-v2.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Five Golden Globe Nominations for ‘Carol’ and a Nod to DiCaprio        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085795" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/movies/golden-globes-2016-film-surprises-and-snubs.html">
            The Carpetbagger: Golden Globes 2016: Film Surprises and Snubs        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085865" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/arts/television/golden-globes-2016-tv-snubs-and-surprises.html">
            Golden Globes 2016: TV Snubs and Surprises        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/politics/index.html">Politics &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004086659" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/politics/first-draft/2015/12/10/franklin-roosevelts-granddaughter-criticizes-donald-trump-over-muslim-remarks/">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/us/10firstdraft-roosevelt/10firstdraft-roosevelt-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        First Draft: Franklin Roosevelt’s Granddaughter Criticizes Donald Trump Over Muslim Remarks        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083625" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/books/arthur-t-hadley-writer-of-critiques-on-us-military-dies-at-91.html">
            Arthur T. Hadley, Writer of Critiques on U.S. Military, Dies at 91        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004086597" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/politics/first-draft/2015/12/10/bob-vander-plaats-influential-iowa-conservative-endorses-ted-cruz/">
            First Draft: Bob Vander Plaats, Influential Iowa Conservative, Endorses Ted Cruz        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/fashion/index.html">Fashion & Style &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004079388" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/13/fashion/the-heart-said-yes-the-hindu-horoscope-said-no.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/fashion/13MODERN/13MODERN-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Modern Love: The Heart Said Yes; the Hindu Horoscope Said No        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004080986" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/fashion/big-names-tiny-homes.html">
            Tiny Homes You Can Collect        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004081321" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/fashion/matthew-moneypenny-trunk-agency.html">
            The Man Who Would Make the World a Prettier Place        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/movies/index.html">Movies &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004075803" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/movies/golden-globes-2016-nominations.html">

        
        <h3 class="story-heading">
        Five Golden Globe Nominations for ‘Carol’ and a Nod to DiCaprio        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085795" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/movies/golden-globes-2016-film-surprises-and-snubs.html">
            The Carpetbagger: Golden Globes 2016: Film Surprises and Snubs        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085865" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/arts/television/golden-globes-2016-tv-snubs-and-surprises.html">
            Golden Globes 2016: TV Snubs and Surprises        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/nyregion/index.html">New York &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004085925" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/nyregion/cuomo-task-force-signals-further-retreat-from-common-core-school-standards.html">

        
        <h3 class="story-heading">
        Cuomo Task Force Signals Further Retreat From Common Core School Standards        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004086346" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/nyregion/connecticut-to-ban-gun-sales-to-those-on-federal-terrorism-lists.html">
            Connecticut to Ban Gun Sales to Those on Federal Terrorism Lists        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084487" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/nyregion/dean-skelos-adam-skelos-corruption-trial.html">
            Jury Begins Deliberations in Trial of Dean Skelos and His Son        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/sports/index.html">Sports &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004085793" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/sports/muhammad-ali-defends-islam-after-trumps-remarks.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/business/11xp-ali/11xp-ali-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Muhammad Ali Defends Islam After Trump’s Remarks        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083959" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/sports/ncaafootball/as-heisman-trophy-finalists-experience-a-milestone-so-do-gamblers.html">
            As Heisman Trophy Finalists Experience a Milestone, So Do Gamblers        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000003862319" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/sports/basketball/dolph-schayes-a-bridge-to-the-modern-nba-is-dead-at-87.html">
            Dolph Schayes, a Bridge to Modern Basketball, Is Dead at 87        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://theater.nytimes.com/">Theater &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004081478" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/13/theater/best-broadway-off-broadway-2015.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/theater/13BESTTHEATER-ALT/13BESTTHEATER-ALT-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        The Best in Culture 2015: The Best Theater of 2015        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004072464" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/theater/review-the-golden-bride-walks-down-the-aisle-again.html">
            Review: ‘The Golden Bride’ Walks Down the Aisle Again        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004072468" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/theater/review-andrea-mcardle-and-kip-gilman-puzzle-out-love-in-2-across.html">
            Review: Andrea McArdle and Kip Gilman Puzzle Out Love in ‘2 Across’        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/section/science">Science &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004086351" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/science/cornell-announces-a-litter-of-puppies-born-using-ivf.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/11/garden/ivf-puppies-video/ivf-puppies-video-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        First I.V.F. Puppies Are Born in Breakthrough at Cornell        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084689" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/science/an-advance-in-artificial-intelligence-rivals-human-vision-abilities.html">
            An Advance in Artificial Intelligence Rivals Human Abilities        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084481" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/world/delegates-at-climate-talks-focus-on-saving-the-worlds-forests.html">
            Delegates at Climate Talks Focus on Saving the World’s Forests        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/section/obituaries">Obituaries &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000003862319" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/sports/basketball/dolph-schayes-a-bridge-to-the-modern-nba-is-dead-at-87.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/11/sports/schayes-obit-web/schayes-obit-web-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Dolph Schayes, a Bridge to Modern Basketball, Is Dead at 87        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083625" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/books/arthur-t-hadley-writer-of-critiques-on-us-military-dies-at-91.html">
            Arthur T. Hadley, Writer of Critiques on U.S. Military, Dies at 91        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083498" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/world/middleeast/fatema-mernissi-a-founder-of-islamic-feminism-dies-at-75.html">
            Fatema Mernissi, a Founder of Islamic Feminism, Dies at 75        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/arts/television/index.html">Television &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004085865" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/arts/television/golden-globes-2016-tv-snubs-and-surprises.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2014/12/23/arts/mozart/mozart-thumbStandard-v3.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Golden Globes 2016: TV Snubs and Surprises        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004078684" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/09/arts/television/season-2-of-transparent-expands-its-view-of-the-pfefferman-clan.html">
            Critic's Notebook: Season 2 of ‘Transparent’ Expands Its View of the Pfefferman Clan        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004081998" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/arts/television/christmas-shows-are-tvs-december-comfort-food.html">
            Christmas Shows Are TV’s December Comfort Food        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/health/index.html">Health &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004083990" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/10/health/happiness-health-study.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/science/10UNHAPPY1/10UNHAPPY1-thumbStandard-v2.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Happiness Doesn’t Bring Good Health, Study Finds        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085670" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://well.blogs.nytimes.com/2015/12/10/so-lonely-it-hurts/">
            Well: So Lonely It Hurts        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004085838" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://well.blogs.nytimes.com/2015/12/10/trying-to-beat-my-25-year-old-self/">
            Well: Trying to Beat My 25-Year-Old Self        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/section/travel">Travel &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004078889" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/13/travel/fortress-resort-in-alberta-slowly-catching-its-breath.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/travel/13FORTRESS/13FORTRESS-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Fortress Resort in Alberta Slowly Catching Its Breath        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000003564417" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/13/travel/skiing-children-new-hampshire.html">
            The Ups and Downs of Learning to Ski        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004039936" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/13/travel/ski-holidays-resorts-aspen-utah.html">
            9 Things Every Skier Should Know This Winter        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/books/index.html">Books &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004086620" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/books/the-top-books-of-2015.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/11/books/11BESTBOOKS/11BESTBOOKS-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        The Best in Culture 2015: The Top Books of 2015        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004081999" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/books/review-the-verdict-speaks-in-a-grisham-esque-tongue-with-a-british-accent.html">
            Books of The Times: Review: ‘The Verdict’ Speaks in a Grisham-esque Tongue With a British Accent        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004070744" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/interactive/2015/12/02/books/review/best-books-of-2015.html">
            The 10 Best Books of 2015        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/education/index.html">Education &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004081961" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/10/us/politics/senate-approves-overhaul-of-no-child-left-behind-law.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/us/10childrenweb/10childrenweb-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Senate Approves Overhaul of No Child Left Behind Law        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004075528" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/nyregion/seton-hall-plans-medical-school-on-former-site-of-drug-company.html">
            New Jersey Officials See Hope for Jobs in Seton Hall’s Medical School Plan        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000003991092" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/11/01/education/edlife/everything-you-need-to-know-about-the-new-sat.html">
            Everything You Need to Know About the New SAT        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/dining/index.html">Food &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004083960" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/16/dining/best-cheap-eats-nyc-2015.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/16/dining/16HUNGRY-slide-6AJF/16HUNGRY-slide-6AJF-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Hungry City: The Top 10 Places to Eat Well and Cheaply in 2015        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004075454" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/16/dining/best-wines-2015.html">
            The Pour: A Top 10 Wine List So Good, It Takes 12 Bottles to Hold It        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000003858372" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/09/dining/jacques-pepin-food-memories.html">
            Jacques Pépin’s Food Memories        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/opinion/index.html#sundayreview">Sunday Review &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004073048" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/06/opinion/sunday/born-to-be-conned.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/06/opinion/sunday/06konnikova/06konnikova-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Opinion: Born to Be Conned        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004074559" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/05/opinion/end-the-gun-epidemic-in-america.html">
            Editorial: End the Gun Epidemic in America        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004073045" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/06/opinion/sunday/women-in-combat-jobs.html">
            Editorial: Women in Combat Jobs        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/realestate/index.html">Real Estate &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004084167" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/13/realestate/a-surprise-addresssecaucus-nj.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/realestate/13HUNT-MAIN/13HUNT-MAIN-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        The Hunt: A Surprise Address: Secaucus, N.J.        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004081772" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/13/realestate/rego-park-queens-finding-value-in-a-melting-pot.html">
            Living In: Rego Park, Queens: Finding Value in a Melting Pot        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004081549" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/realestate/compare-homes-in-weybridge-vermont-columbus-ohio-cave-creek-arizona-weybridge-vermont.html">
            What You Get: $1.5 Million Homes in Vermont, Ohio and Arizona        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/upshot/">The Upshot &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004083941" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/10/upshot/signs-of-a-truce-in-the-mommy-wars.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/business/09UP-Parents/09UP-Parents-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Modern Families: Signs of a Truce in the Mommy Wars        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083248" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/11/upshot/nfl-playoff-scenarios-watch-week-14-like-an-expert.html">
            Smarter Football: N.F.L. Playoff Scenarios: Watch Week 14 Like an Expert        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083569" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/upshot/trump-is-wrong-muslims-in-us-are-less-likely-to-endorse-violence.html">
            Public Opinion: Trump Is Wrong. Muslims in U.S. Are Less Likely to Endorse Violence.        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/section/magazine">Magazine &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004075165" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/13/magazine/letter-of-recommendation-smartwool-socks.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/13/magazine/13lor/13mag-13lor-t_CA0-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Letter of Recommendation: Letter of Recommendation: SmartWool Socks        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004076230" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/13/magazine/the-art-of-flying-in-the-movies.html">
            Great Performers: The Art of Flying in the Movies        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004076122" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/13/magazine/editors-letter-take-flight-with-virtual-reality.html">
            Great Performers: Editor’s Letter: Take Flight With Virtual Reality        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

        
            <div class="split-3-layout layout theme-base">

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/pages/automobiles/index.html">Automobiles &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004085561" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/11/business/international/vw-emissions-scandal.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/11/business/11Volkswagen-web2/11Volkswagen-web2-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        VW Says Emissions Cheating Was Not a One-Time Error        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004067893" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/04/automobiles/autoreviews/video-review-honda-pilot-is-useful-like-a-van-but-no-longer-square.html">
            Driven: Video Review: Honda Pilot Is Useful Like a Van, but No Longer Square        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004035284" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/08/business/energy-environment/despite-push-for-cleaner-cars-sheer-numbers-could-work-against-climate-benefits.html">
            Despite Push for Cleaner Cars, Sheer Numbers Could Work Against Climate Benefits        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column ">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/section/t-magazine">T Magazine &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004086013" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/10/t-magazine/art/plant-photography-alice-cazenave.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/10/t-magazine/10tmag-viewfinder-slide-9R3I/10tmag-viewfinder-slide-9R3I-thumbStandard.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        Viewfinder: Delicate, Magical Portraits of Plants’ Inner Lives        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004084613" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/t-magazine/travel/colmar-france-museum-makeover.html">
            Art: An Old Alsatian Museum’s Modern Makeover        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004083213" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/10/t-magazine/chcm-fall-winter-2015-mens-collection-debut.html">
            Men's Fashion: A Men’s Wear Staple Expands its Offerings        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
                    <div class="column last-column">

                        <section>
    <h2 class="section-heading">
        <a href="http://www.nytimes.com/times-insider/">Times Insider &raquo;</a>
    </h2>
    <ul>
                    <li>
            <article class="story theme-summary" data-story-id="100000004080988" data-rank="0" data-collection-renderstyle="HpHedThumbWell">

    <a class="story-link" href="http://www.nytimes.com/2015/12/08/insider/new-critic-at-large-breathtakingly-funny-absolutely-serious.html">

                    <div class="thumb">
                <img src="http://static01.nyt.com/images/2015/12/08/insider/08-insider-morris-photo/08-insider-morris-photo-thumbStandard-v2.jpg" alt="" />
            </div>
        
        <h3 class="story-heading">
        New Critic at Large: ‘Breathtakingly Funny, Absolutely Serious’        </h3>
    </a>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004079359" data-rank="1" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/07/insider/the-gun-epidemic-the-making-of-a-page-1-editorial.html">
            The Gun Epidemic: The Making of a Page 1 Editorial        </a>
    </h2>
</article>
            </li>
                    <li>
            <article class="story" data-story-id="100000004074681" data-rank="2" data-collection-renderstyle="HpHedThumbWell">
    <h2 class="story-heading">
        <a href="http://www.nytimes.com/2015/12/04/insider/learning-from-ebola-was-quarantine-too-extreme-a-response.html">
            Learning From Ebola: Was Quarantine Too Extreme a Response?        </a>
    </h2>
</article>
            </li>
            </ul>
</section>

                    </div> <!-- close column -->

                                
            </div><!-- close split-3-layout -->

            </div>
</section><!-- close well section -->

    </div><!-- close ab-column -->

    <div class="c-column column">

        
                    <section class="real-estate">
                <div class="layout split-layout theme-base">
                    <div class="column">
                        <div class="region real-estate-left-region">

                            <div class="collection">
                <h2 class="section-heading">
            <a href="http://www.nytimes.com/pages/realestate/index.html">Real Estate &raquo;</a>
        </h2>
        <article class="story theme-summary" id="topnews-100000004084167" data-story-id="100000004084167" data-rank="0" data-collection-renderstyle="HpSectionSumSmallMedia">
    
            <h3 class="kicker">The Hunt </h3>
    
    <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/13/realestate/a-surprise-addresssecaucus-nj.html">A Surprise Address: Secaucus, N.J.</a></h2>

            <p class="byline">By JOYCE COHEN </p>
    
            <div class="thumb">
            <a href="http://www.nytimes.com/2015/12/13/realestate/a-surprise-addresssecaucus-nj.html"><img src="http://static01.nyt.com/images/2015/12/13/realestate/13HUNT-MAIN/13HUNT-MAIN-thumbStandard.jpg" alt=""></a>
        </div>
    
    <p class="summary">
        Ronald Valverde and Leslie Valverde Ayers wanted to buy an apartment. The best deal they found was in Secaucus, N.J.    </p>

    
        <ul class="refer theme-news-headlines">
            <li>
            <article class="story" id="topnews-100000004084167">
                <h2 class="refer-heading"><a href="http://www.nytimes.com/column/the-hunt">More Articles in the Series</a></h2>
            </article>
        </li>
        </ul>
</article>

</div>
<div class="collection">
            <ul class="theme-module-headlines">
<li><article class="story" data-collection-renderstyle="HpHeadline"><h2 class="story-heading"><a href="http://www.nytimes.com/real-estate/find-a-home">Search for Homes for Sale or Rent</a></h2></article></li>

<li><article class="story" data-collection-renderstyle="HpHeadline"><h2 class="story-heading"><a href="http://realestateads.nytimes.com/">Sell Your Home</a></h2></article></li></ul>

</div>

                        </div><!-- close real-estate-left-region -->
                    </div>
                    <div class="column">
                        <div class="region real-estate-right-region">

                            <div class="collection">
            <div id="HPmodule-RE2-alternate-content"><div></div>
<div class="collection">
                <h3 class="kicker collection-kicker">&nbsp;</h3>
        <article class="story theme-summary" id="topnews-100000004082278" data-story-id="100000004082278" data-rank="0" data-collection-renderstyle="HpSumSmallMedia">
    
            <h3 class="kicker">International Real Estate </h3>
    
    <h2 class="story-heading"><a href="http://www.nytimes.com/2015/12/09/realestate/real-estate-in-colombia.html">House Hunting in ... Colombia</a></h2>

            <p class="byline">By MARCELLE SUSSMAN FISCHLER </p>
    
            <div class="thumb">
            <a href="http://www.nytimes.com/2015/12/09/realestate/real-estate-in-colombia.html"><img src="http://static01.nyt.com/images/2015/12/09/realestate/09IHH-slide-MMXS/09IHH-slide-MMXS-thumbStandard.jpg" alt=""></a>
        </div>
    
    <p class="summary">
        High-end properties appreciated more than 10 percent over the last year. And the devaluation of the Colombian peso is attracting more dollar-paying buyers.    </p>

    
        <ul class="refer theme-news-headlines">
            <li>
            <article class="story" id="topnews-100000004082278">
                <h2 class="refer-heading"><a href="http://www.nytimes.com/column/international-real-estate">More Articles in the Series</a></h2>
            </article>
        </li>
        </ul>
</article>

</div>
<div class="collection">
            </div></div></div>

                            <div id="HPmodule-RE2" class="ad hpmodule-re2-ad nocontent robots-nocontent"></div>
                        </div><!-- close real-estate-right-region -->
                    </div>
                </div>
            </section>
        
        
        <section id="recommendations" class="recommendations">
    <div class="tabs-container">
        <ul class="tabs">
            <li data-type="most-emailed"><a href="http://www.nytimes.com/gst/mostemailed.html">Most Emailed</a></li><li data-type="most-viewed"><a href="http://www.nytimes.com/gst/mostpopular.html">Most Viewed</a></li><li data-type="recommended"><a href="http://www.nytimes.com/recommendations">Recommended for you</a></li>
        </ul>
    </div>
    <div class="tab-content most-emailed">
        <div class="loader"></div>
    </div>
    <div class="tab-content most-viewed">
        <div class="loader"></div>
    </div>
    <div class="tab-content recommended">
        <div class="loader"></div>
    </div><!-- end tabs-container -->
    <div id="recommendations-module-loader" class="loader-container">
        <div class="loader loader-t-logo-32x32-ecedeb-ffffff"><span class="visually-hidden">Loading...</span></div>
    </div>
</section>

        <div id="Box1" class="ad box1-ad nocontent robots-nocontent"></div>
        <div id="HPPromo" class="ad hppromo-ad nocontent robots-nocontent"></div>

        
        <div id="HPBottom1" class="ad hpbottom1-ad nocontent robots-nocontent"></div>

    </div><!-- close c-column -->

</div><!-- close span-ab-layout -->

<div id="HPSponLink" class="ad hpsponlink-ad nocontent robots-nocontent"></div>
<div id="Bottom8" class="ad bottom8-ad nocontent robots-nocontent"></div>
<div id="Bottom9" class="ad bottom9-ad nocontent robots-nocontent"></div>
                    <div class="search-overlay"></div>
            </main><!-- close main -->
            <section id="site-index" class="site-index">
    <header class="section-header">
        <p class="user-action"><a href="http://www.nytimes.com/">Go to Home Page &raquo;</a></p>
        <h2 class="section-heading">
            <span class="visually-hidden">Site Index</span>
            <a id="site-index-branding-link" href="http://www.nytimes.com/">
                <span class="visually-hidden">The New York Times</span>
            </a>
        </h2>
        <script>window.magnum.writeLogo('small', 'http://a1.nyt.com/assets/homepage/20151207-151834/images/foundation/logos/', '', '', 'standard', 'site-index-branding-link', '');</script>
    </header>

    <nav id="site-index-navigation" class="site-index-navigation" role="navigation">

        <div class="split-6-layout layout">

                                                                                    
                    <div class="column">
                        <h3 class="menu-heading">News</h3>
                        <ul class="menu">

                                                                                                                                                                                                                                                
                                    <li>
                                        <a href="http://www.nytimes.com/pages/world/index.html">World</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/national/index.html">U.S.</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/politics/index.html">Politics</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/nyregion/index.html">N.Y.</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/business/index.html">Business</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/technology/index.html">Tech</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/section/science">Science</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/health/index.html">Health</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/sports/index.html">Sports</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/education/index.html">Education</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/obituaries/index.html">Obituaries</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/todayspaper/index.html">Today's Paper</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/corrections/index.html">Corrections</a>
                                    </li>

                                                            
                        </ul>
                    </div><!-- close column -->

                                            
                    <div class="column">
                        <h3 class="menu-heading">Opinion</h3>
                        <ul class="menu">

                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html">Today's Opinion</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#columnists">Op-Ed Columnists</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#editorials">Editorials</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#contributing">Contributing Writers</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#op-ed">Op-Ed Contributors</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#opinionator">Opinionator</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#letters">Letters</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#sundayreview">Sunday Review</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/opinion/index.html#takingNote">Taking Note</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/roomfordebate">Room for Debate</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://topics.nytimes.com/top/opinion/thepubliceditor/index.html">Public Editor</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/video/opinion">Video: Opinion</a>
                                    </li>

                                                            
                        </ul>
                    </div><!-- close column -->

                                            
                    <div class="column">
                        <h3 class="menu-heading">Arts</h3>
                        <ul class="menu">

                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/arts/index.html">Today's Arts</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/arts/design/index.html">Art & Design</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://artsbeat.blogs.nytimes.com/">ArtsBeat</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/books/index.html">Books</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/arts/dance/index.html">Dance</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/movies/index.html">Movies</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/arts/music/index.html">Music</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/events/">N.Y.C. Events Guide</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/arts/television/index.html">Television</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/theater/index.html">Theater</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/arts/video-games/index.html">Video Games</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/video/arts">Video: Arts</a>
                                    </li>

                                                            
                        </ul>
                    </div><!-- close column -->

                                            
                    <div class="column">
                        <h3 class="menu-heading">Living</h3>
                        <ul class="menu">

                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/automobiles/index.html">Automobiles</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/crosswords/">Crossword</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/dining/index.html">Food</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/education/index.html">Education</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/fashion/index.html">Fashion & Style</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/health/index.html">Health</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/section/jobs">Jobs</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/magazine/index.html">Magazine</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/events/">N.Y.C. Events Guide</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/realestate/index.html">Real Estate</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/section/t-magazine">T Magazine</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/travel/index.html">Travel</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/fashion/weddings/index.html">Weddings & Celebrations</a>
                                    </li>

                                                            
                        </ul>
                    </div><!-- close column -->

                                            
                    <div class="column">
                        <h3 class="menu-heading">Listings & More</h3>
                        <ul class="menu">

                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/ref/classifieds/">Classifieds</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/marketing/tools-and-services/">Tools & Services</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/topics/">Times Topics</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://topics.nytimes.com/top/opinion/thepubliceditor/index.html">Public Editor</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/events/">N.Y.C. Events Guide</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://tvlistings.nytimes.com/tvlistings/ZCGrid.do?aid=nyt">TV Listings</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/interactive/blogs/directory.html">Blogs</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/pages/multimedia/index.html">Multimedia</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://lens.blogs.nytimes.com/">Photography</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/video">Video</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="https://www.nytimes.com/store/?&t=qry542&utm_source=nytimes&utm_medium=HPB&utm_content=hp_browsetree&utm_campaign=NYT-HP&module=SectionsNav&action=click&region=TopBar&version=BrowseTree&contentCollection=NYT%20Store&contentPlacement=2&pgtype=Homepage">NYT Store</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/times-journeys/?utm_source=nytimes&utm_medium=HPLink&utm_content=hp_browsetree&utm_campaign=NYT-HP">Times Journeys</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/seeallnav">Subscribe</a>
                                    </li>

                                                                                            
                                    <li>
                                        <a href="http://www.nytimes.com/membercenter">Manage My Account</a>
                                    </li>

                                                            
                        </ul>
                    </div><!-- close column -->

                            
            <div class="column last-column">
                
<h3 class="menu-heading">Subscribe</h3>

<ul class="menu primary-menu">
    <li class="menu-label">Subscribe</li>
    <li class="times-premier">
        <i class="icon sprite-icon"></i>
        <a href="http://www.nytimes.com/tpnav">Times Insider</a>
    </li>

    <li class="home-delivery">
        <i class="icon sprite-icon"></i>
                    <a href="http://www.nytimes.com/hdleftnav">Home Delivery</a>
            </li>
    <li class="digital-subscriptions">
        <i class="icon sprite-icon"></i>
                    <a href="http://www.nytimes.com/digitalleftnav">Digital Subscriptions</a>
            </li>
    <li class="nyt-opinion">
        <i class="icon sprite-icon"></i>
        <a href="http://www.nytimes.com/opinionindex">NYT Opinion</a>
    </li>
    <li class="nyt-crossword last-item">
        <i class="icon sprite-icon"></i>
        <a id="nyt-crossword" href="http://www.nytimes.com/crosswords/index.html">Crossword</a>
    </li>

</ul>

<ul class="menu secondary-menu">

    <li>
        <a href="http://www.nytimes.com/marketing/newsletters">Email Newsletters</a>
    </li>
    <li>
        <a href="https://myaccount.nytimes.com/mem/tnt.html">Alerts</a>
    </li>
    <li>
                    <a href="http://www.nytimes.com/giftleftnav">Gift Subscriptions</a>
            </li>
    <li>
                    <a href="http://www.nytimes.com/corporateleftnav">Corporate Subscriptions</a>
            </li>
    <li>
                    <a href="http://www.nytimes.com/educationleftnav">Education Rate</a>
            </li>

</ul>
<ul class="menu secondary-menu">
    <li>
        <a href="http://www.nytimes.com/services/mobile/index.html">Mobile Applications</a>
    </li>
    <li>
                    <a href="http://eedition.nytimes.com/cgi-bin/signup.cgi?cc=37FYY">Replica Edition</a>
            </li>
                <li>
            <a href="http://subscribe.inyt.com">International New York Times</a>
        </li>
    
</ul>

            </div><!-- close column -->

        </div><!-- close split-6-layout -->

    </nav><!-- close nav -->

</section><!-- close site-index -->

            <footer id="page-footer" class="page-footer" role="contentinfo">
    <nav>
         <ul>
             <li>
                <a href="http://www.nytimes.com/content/help/rights/copyright/copyright-notice.html" itemprop="copyrightNotice">
                    &copy; <span itemprop="copyrightYear">2015</span><span itemprop="copyrightHolder provider sourceOrganization" itemscope itemtype="http://schema.org/Organization" itemid="http://www.nytimes.com"><span itemprop="name"> The New York Times Company</span><meta itemprop="tickerSymbol" content="NYSE NYT"/></span>
                </a>
            </li>
            <li class="wide-viewport-item"><a href="http://www.nytimes.com/ref/membercenter/help/infoservdirectory.html">Contact Us</a></li>
            <li class="wide-viewport-item"><a href="http://www.nytco.com/careers">Work With Us</a></li>
            <li class="wide-viewport-item"><a href="http://www.nytimes.whsites.net/mediakit">Advertise</a></li>
            <li class="wide-viewport-item"><a href="http://www.nytimes.com/content/help/rights/privacy/policy/privacy-policy.html#pp">Your Ad Choices</a></li>
            <li><a href="http://www.nytimes.com/privacy">Privacy</a></li>
            <li><a href="http://www.nytimes.com/ref/membercenter/help/agree.html" itemprop="usageTerms">Terms of Service</a></li>
            <li class="wide-viewport-item last-item"><a href="http://www.nytimes.com/content/help/rights/sale/terms-of-sale.html">Terms of Sale</a></li>
         </ul>
    </nav>
    <nav class="last-nav">
        <ul>
            <li><a href="http://spiderbites.nytimes.com">Site Map</a></li>
            <li><a href="http://www.nytimes.com/membercenter/sitehelp.html">Help</a></li>
            <li><a href="https://myaccount.nytimes.com/membercenter/feedback.html">Site Feedback</a></li>
            <li class="wide-viewport-item last-item"><a href="http://www.nytimes.com/subscriptions/Multiproduct/lp5558.html?campaignId=37WXW">Subscriptions</a></li>
        </ul>
    </nav>

    <div id="mobile-banner" class="mobile-banner hidden">
        <a class="banner-message" href="http://mobile.nytimes.com/">View Mobile Version</a>
    </div>

    <div id="dfp-perf-test" class="ad hidden"></div>
</footer>
        </div><!-- close page -->
    </div><!-- close shell -->
    <script>
require(['foundation/main'], function () {
    require(['homepage/main']);
    require(['jquery/nyt', 'foundation/views/page-manager'], function ($, pageManager) {
        if (window.location.search.indexOf('disable_tagx') > 0) {
            return;
        }
        $(document).ready(function () {
            require(['http://static01.nyt.com/bi/js/tagx/tagx.js'], function () {
                pageManager.trackingFireEventQueue();
            });
        });
    });
});
</script>

    <div id="Inv1" class="ad inv1-ad hidden"></div>
<div id="Inv2" class="ad inv2-ad hidden"></div>
<div id="Inv3" class="ad inv3-ad hidden"></div>
<div id="ab1" class="ad ab1-ad hidden"></div>
<div id="ab2" class="ad ab2-ad hidden"></div>
<div id="ab3" class="ad ab3-ad hidden"></div>
<div id="prop1" class="ad prop1-ad hidden"></div>
<div id="prop2" class="ad prop2-ad hidden"></div>
<div id="Anchor" class="ad anchor-ad hidden"></div>
<div id="ADX_CLIENTSIDE" class="ad adx-clientside-ad hidden"></div>
<script type="text/javascript">window.NREUM||(NREUM={});NREUM.info={"beacon":"bam.nr-data.net","licenseKey":"b5bcf2eba4","applicationID":"4491938","transactionName":"YwFXZhRYVhAEVUZcX1pLYEAPFlkTFRhCXUA=","queueTime":0,"applicationTime":589,"ttGuid":"","agentToken":"","userAttributes":"","errorBeacon":"bam.nr-data.net","agent":"js-agent.newrelic.com\/nr-686.min.js"}</script></body>
</html>
