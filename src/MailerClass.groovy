import java.awt.*

class EMailerClass {
    def log ;
    def context;
    def testRunner;

    MailerClass(logIn, contextIn, testRunnerIn){
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
    }
    def RequestMail(String mailText){
        String subject = "Request "
        String body = getBody(bodyText)
        String cc = ""
        try {
            Desktop.getDesktop().mail(new URI("mailto:RECIPENTADDRESS?subject=" + subject + "&cc" + cc + "&body=" + body))
        }catch(IOException e){
            log.error(e.toString())
        }

    }
    String getBody(String bodyText){
        StringBuilder body = new StringBuilder();
        String newLine = "%0D%0A"
        String result

        body.append("Hello team, ") + newLine + bodyText + newLine
        result = body.toString()
        result = result.replace(' ', "%2O")
        result = result.replace('>', "%3E")
        result = result.replace('&', "%26")
        result = result.replace('$', "%24")
        result = result.replace('=', "%3D")
        result = result.replace('/', "%2F")
        return result

    }
}
context.setProperty('mailer', new EMailerClass(log,context,testRunner))
