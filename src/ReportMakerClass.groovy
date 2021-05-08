class ReportMaker {
    def log
    def context
    def testRunner
    def cur_time
    String storePath

    ReportMaker(logIn, contextIn, testRunnerIn){
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
    }
    def makeCSVReport(){
        def project = testRunner.testCase.testSuite.project.workspace.getProjectByName("ScriptLibrary")
        def tCase = project.testSuites["Library"].testCases["Classes"]
        def tStep = tCase.getTestStepByName("PathProviderClass")
        def runner = tStep.run (testRunner, context)
        //log.info("runner status---------> " + runner.status)
        def path = context.path
        storePath = path.GetPath()

        //elements we want in the report
        def suite = testRunner.testCase.testSuite.getName()
        def testCase = testRunner.testCase.getName()


        def reportFile = new File(storePath, "Report_" + testRunner.testCase.testSuite.project.name.toString() + ".csv")
        if (!reportFile.exists()){
            reportFile.createNewFile()
            //adding file headers
            reportFile.write("Test Suite","Test Case")
        }
        reportFile.append('\n')
        reportFile.append(suite + ",")
        reportFile.append(testCase + ",")

        reportFile.append('\n')

    }
}
context.setProperty("reporter", new ReportMaker(log,context, testRunner))