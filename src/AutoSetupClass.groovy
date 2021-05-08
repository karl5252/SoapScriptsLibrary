import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.UIManager
import java.awt.Dimension

class SetupClass {
    def log
    def context
    def testRunner
    def projectCount
    String setupScriptText = '''

def project = project
if (project.getPropertyValue("autoSetupProperties") == null ||  
project.getPropertyValue("autoSetupProperties") == "" ||
project.getPropertyValue("autoSetupProperties") == " "){
        project.setPropertyValue("autoSetupProperties", "false")
        project.setPropertyValue("resetGlobals", "false")
        return
    }
    if(project.hasProperty("autoSetupProperties") && project.getPropertyValue("autoSetupProperties") == "true"){

        //clean
        String [] propToRemove1 = new String[project.getPropertyCount()]
        propToRemove1 = project.getPropertyNames()
        for (int i = 0; i < propToRemove1.size(); i++){
            project.removeProperty(propToRemove1[i])
        }
        project.setPropertyValue("outputPath", "")
        def testStepCount = 0
        for (int z = 0; z < project.getTestSuiteCount();z++){
            def testSuite = project.getTestSuiteAt(z)
            def testCaseCount = testSuite.getTestCaseCount()
            for (int j = 0; j < testCaseCount; j++){
                def testCase = testSuite.getTestCaseAt(j)
                String[] propToRemove = new String [testCase.getPropertyCount()]
                propToRemove = testCase.getPropertyNames()
                for(int i = 0; i < propToRemove.size(); i++){
                    testCase.removeProperty(propToRemove[i])
                }
                //properties to be added instead of those removed by above code
                testCase.setPropertyValue("Request_Property1","1")
                testCase.setPropertyValue("Request_Property2","2")
                testCase.setPropertyValue("Request_Property3","3")
                testCase.setPropertyValue("Request_Property4","4")
                testCase.setPropertyValue("Request_Property5","5")
                
                testStepCount = testStepCount + testCase.getPropertyCount()
                
            }
        }
            project.setPropertyValue("autoSetupProperties", "false")
    }
    if (project.getPropertyValue("resetGlobals") == null ||
    project.getPropertyValue("resetGlobals") == "" ||
    project.getPropertyValue("resetGlobals") == " "){
        project.setPropertyValue("resetGlobals", "false")
    }else{
        com.eviware.soapui.SoapUI.globalProperties.setPropertyValue("Shared_Property1","1")
        com.eviware.soapui.SoapUI.globalProperties.setPropertyValue("Shared_Property2","2")
        com.eviware.soapui.SoapUI.globalProperties.setPropertyValue("Shared_Property3","3")
        com.eviware.soapui.SoapUI.globalProperties.setPropertyValue("Shared_Property4","4")
        com.eviware.soapui.SoapUI.globalProperties.setPropertyValue("Shared_Property5","5")
        //revert property status back to false to avoid unwanted clean up of data
        project.setPropertyValue("resetGlobals", "false")
    }





'''

    AutoSetupClass(logIn, contextIn, testRunnerIn) {
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn

        projectCount = testRunnerIn.testCase.testSuite.project.workspace.getProjectCount()

    }

    void SetProjects() {
        def alert = com.eviware.soapui.support.UISupport
        if (projectCount == 1) {
            JPanel panel = new JPanel()
            panel.setSize(250, 50)
            JLabel label = new JLabel("Do you want to load project from CSV?")
            UIManager.put("OptionPane.minimumSize", new Dimension(400, 200))
            int res = JOptionPane.showConfirmDialog(null, label, "No projects found", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)

            if (res == 0) {
                alert.showInfoMessage("Coming Soon!")
            } else if (res == 1) {
                alert.showInfoMessage("Please create or add existing project and rerun this script. Thank you")
            } else {
            }


        } else {
            for (int i = 0; i < projectCount; i++) {
                def projectIndex = testRunner.testCase.testSuite.project.workspace.getProjectAt(i)

                if (projectIndex.getPropertyValue("autoSetupProperties") == null ||
                        projectIndex.getPropertyValue("autoSetupProperties") == "" ||
                        projectIndex.getPropertyValue("autoSetupProperties") == " ") {
                    projectIndex.setPropertyValue("autoSetupProperties", "false")
                    projectIndex.setPropertyValue("resetGlobals", "false")
                    continue

                }
            }
        }
    }

    void SetupProperties() {
        if (projectCount > 0) {
            for (int i = 0; i < projectCount; i++) {
                def projectIndex = testRunner.testCase.testSuite.project.workspace.getProjectAt(i)
                projectIndex.setAfterLoadScript(setupScriptText)
            }
        }
    }
    void LoadProjects(){
        //swing prompt for path for csv file
        //check file
        //load project structure from the file
    }
}

context.setProperty("setup", new SetupClass(log,context,testRunner))
