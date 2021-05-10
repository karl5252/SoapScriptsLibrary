class RequestTeardown {
    def log
    def context
    def testRunner

    RequestTeardown(logIn, contextIn, testRunnerIn){
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
    }

    def addAssertionToSoapStep(String typeOfAssert, boolean cleanMode){
        //def currentSuite =  context.testCase.testSuite.name
        //def currentCase = context.testCase.name
        def soapRequests = testRunner.testCase.getTestStepsOfType(com.eviware.soapui.iml.wsdl.teststeps.WsdlTestRequestStep)

        String assertionTeardownScript = '''
def response = messageExchange.getResponseContent().toString()

checkResponseNode(response, "nodeName", "SaveUnderThisPropertyName")  //repeat as many times necessary

def checkResponseNode(String response, String nodeName, String propertyName){
def xml = new XmlSlurper().parseText(response)
def nodeVal = xml.depthFirst().findAll { node ->
    (node.name() == nodeName)
}
assert nodeVal.size()>=1
log.info nodeVal
def stringifiedValue = nodeVal.join(", ")
messageExchange.modelItem.testStep.testCase.setPropertyValue(propertyName, stringifiedValue)
}

'''

        def each = soapRequests.each {
            def assertionList = testRunner.getTestCase().getTestStepByName(it.name).getAssertionList()
            String soapStepName = it.name
            if(cleanMode == true){
                for (e in assertionList) {
                    testRunner.getTestCase().getTestStepByName(soapStepName).removeAssertion(e)
                    def scriptAssertion = testRunner.testCase.testSteps[soapStepName].addAssertion("Script Assertion")
                    scriptAssertion.setName("Request Teardown")
                    scriptAssertion.setScriptText(assertionTeardownScript)
                    break;
                }
            }

        }
    }


}

context.setProperty("rTeardown", new RequestTeardown (log,context,testRunner))
