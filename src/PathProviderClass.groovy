import com.eviware.soapui.support.GroovyUtils
class PathProvider {
    def log
    def context
    def testRunner
    def cur_time
    def alert = com.eviware.soapui.support.UISupport
    String outputFilePath

    PathProvider(logIn, contextIn, testRunnerIn){
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
        cur_time = new Date().format("yyy-MM-dd HH")
    }
    def GetPath(){
        def projectPath = new GroovyUtils(context).projectPath
        def testProject = testRunner.testCase.testSuite.project
        outputFilePath = testProject.getPropertyValue("outputPath")

        if (outputFilePath == null || outputFilePath == "" || outputFilePath == " "){
            def promptPath = alert.prompt("Output path for your files", "please provide path")
            if (promptPath != null || promptPath != "" || promptPath != " "){
                outputFilePath = promptPath.toString()
                testProject.getPropertyValue("outputPath", promptPath.toString())
            }else {
                tp.setPropertyValue("outputPath", projectPath)
                outputFilePath = projectPath
            }

        }
    }
}
context.setProperty("pathProvider", new PathProvider(log, context,testRunner))
