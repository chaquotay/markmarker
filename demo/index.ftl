<html>
<body>
<#foreach post in posts>
<a href="${post["rel-url"]}">${post.title}</a><br>
<@markdown>
${post.text}
</@markdown>
</#foreach>
</body>
</html>