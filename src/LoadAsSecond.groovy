//import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory

String stepName = "StoreMessagesToFile"
String scriptVal = '''
    def project = testRunner.testCase.testSuite.project.workspace.getProjectByName("ScriptLibrary")
    def tCase = project.testSuites["Library"].testCases["Classes"]
    
    def tStep = tCase.getTestStepByName("RequestHandlerClass")
    def tStep2 = tCase.getTestStepByName("ReportMakerClass")

    def runner = tStep.run (testRunner, context)
    def runner2 = tStep.run (testRunner, context)
    //log.info("runner status---------> " + runner.status)
    
    def requests = context.requests
    def reporter = context.reporter

    requests.ExtractMessages()
    reporter.makeCSVReport()
    '''
String setupScript = '''  //example
    def name = ["Anon" , "Anon2"]
    def name2 = ["Test1", "Test2"]
   Random r1 = new Random()
    Random r2 = new Random()
    
    int idx1 = r1.nextInt(name.size)
    int idx2 = r2.nextInt(name2.size)
    
    if (name[idx1] == null || name[idx1] == "" || name[idx1] == " "){
        name[idx1] == "Test"
    }
    if (name[idx2] == null || name[idx2] == "" || name[idx2] == " "){
        name[idx1] == "Test"
    }
    if (name2[idx1] == null || name2[idx1] == "" || name2[idx1] == " "){
        name2[idx1] == "Test"
    }
    if (name2[idx2] == null || name2[idx2] == "" || name2[idx2] == " "){
        name2[idx2] == "Test"
    }
    String tempName1 = name[idx1] + " " + name2[idx2]
    String tempName2 = name[idx2] + " " + name2[idx1]
    idx1 = idx1 + 1
    testCase.setPropertyValue("Request_RandomFloat", "$idx1.$idx2" )
    testCase.setPropertyValue("Request_RandomName1", "$tempName1")
    testCase.setPropertyValue("Request_RandomName2", "$tempName2")


 
 '''
String assertionCode = '''
  
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



   /* def each = soapRequests.each {
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
}*/
'''
for (testProject in testRunner.testCase.testSuite.project.workspace.getProjectList()){
    for (int i = 0; i < testProject.getTestSuiteCount(); i++){
        def testSuite = testProject.getTestSuiteAt(i)
        def testCaseCount = testSuite.getTestCaseCount()

        caseloop: for (int j = 0; j < testCaseCount; j++){
            def testCase = testSuite.getTestCaseAt(j)
            testCase.setSetupScript(setupScript)

            testCase.getTestStepList().each(){

                if (it.config.type == 'request'){
                    def assertionList = it.getAssertionList()
                    if(assertionList.size() > 0) {
                        for (e in assertionList){
                            it.removeAssertion(e)
                        }
                    }
                    def soapFaultAss = it.addAssertion("Not SOAP Fault")
                    def soapRespAss = it.addAssertion("SOAP Response")
                    def schemaAss = it.addAssertion("Schema Compliance")
                    def requestTeardown = it.addAssertion("Script Assertion")
                    requestTeardown.setName("TeardownScript")
                    requestTeardown.setScriptText(assertionCode)
                }
                if(it.getName() == stepName){
                    //continue
                    testCase.removeTestStep(it)
                }

            }
            addedStep = testCase.addTestStep( GroovyScriptStepFactory.GROOVY_TYPE, stepName)
            addedStep.properties["script"].value = scriptVal
        }
    }
}

