import groovy.xml.XmlSlurper

class RequestHandler {
    def log
    def context
    def testRunner
    def curTime
    String storePath

    RequestHandler(logIn, contextIn, testRunnerIn){
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
        curTime = new Date().format("yyyy-MM-dd_HH-mm-SS")
    }
    def ExtractMessages(){
        def project = testRunner.testCase.testSuite.project.workspace.getProjectByName("ScriptLibrary")
        tCase = project.testSuites["Library"].testCases["Classes"]
        tStep = tCase.getTestStepByName("PathProviderClass")
        def runner = tStep.run (testRunner, context)
        //log.info("runner status---------> " + runner.status)
        def path = context.path
        storePath = path.GetPath()

        def tgResp = com.eviware.soapui.SoapUI.globalProperties
        if (tgResp.hasProperty("ProduceSOAPRequest")) log.info("Propety is there")
        else com.eviware.soapui.SoapUI.globalProperties.setPropertyValue("ProduceSOAPRequest","false")

        String soapSubFolders = path.CreateSubFolder("SOAP")


            def soapRequests = testRunner.testCase.getTestStepsOfType(com.eviware.soapui.imp.wsdl.teststeps.WsdlTestRequestStep)


            def fileName = testRunner.testCase.name + curTime
            fileName = fileName.substring(0,Math.min(filename.length(),10))
            String stepName
        try{
            soapRequests.each{
                //if ID is retunred it can be taken into file name below
            /*def parsedXML =  new XmlSlurper().parseText(context.testCase.getTestStepByName(it.name).getProperty("response").value)
            def nodeCheck = parsedXML.'**'.findAll {node ->
                (node.name() == 'tagName')}*/
                def inputFileRawRequest = new File(soapSubFolders, "RQST"+fileName+".xml")
                def inputFileResponse = new File(soapSubFolders, "RSPS"+fileName+".xml")
                if (inputFileRawRequest.write(context.testCase.getTestStepByName(it.name).getProperty("rawRequest").value) == null){
                    log.error("rawRequest is empty! please check manually for possible errors")
                    Exception exception
                    inputFileResponse.write("Ooops something went wrong. Please check application GUI for details " + exception.toString())
                }else {
                    inputFileRawRequest.write(context.testCase.getTestStepByName(it.name).getProperty("rawRequest").value)
                }
                inputFileResponse.write(context.testCase.getTestStepByName(it.name).getProperty("response").value)

            }

        }catch(IOException e){
            inputFileRawRequest.write("Problem has occured during generation, please rerun  " + e.toString())
            inputFileResponse.write("Problem has occured during generation, please rerun " + e.toString())



        }
        //check if we got JDBCs maybe?
        String jdbcSubFolders = path.CreateSubFolder("JDBC")
        def jdbcRequests = testRunner.testCase.getTestStepsOfType(com.eviware.soapui.imp.wsdl.teststeps.JdbcRequestTestStep)

        try{
            jdbcRequests.each{
                def jdbcOracleOutput = new File(jdbcSubFolders, "RSPS"+fileName+".xml")
                jdbcOracleOutput.write(context.testCase.getTestStepByName(it.name).getProperty("ResponseAsXml").value)
        }


    }catch (Exception e1){
        log.error ("JDBC write failure due to " + e1)
        }
    }
}
context.setProperty("requests", new RequestHandler(log,context,testRunner))
