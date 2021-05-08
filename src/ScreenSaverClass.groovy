import javax.imageio.ImageIO
import java.awt.AWTException
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

class ScreenSaver {
    def log
    def context
    def testRunner

    ScreenSaver(logIn, contextIn, testRunnerIn) {
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
    }
    def TimedScreenshot(def timer){
        def project = testRunner.testCase.testSuite.project.workspace.getProjectByName("ScriptLibrary")
        def tCase = project.testSuites["Library"].testCases["Classes"]
        def tStep = tCase.getTestStepByName("PathProviderClass")
        def runner = tStep.run (testRunner, context)
        //log.info("runner status---------> " + runner.status)
        def path = context.path

        def storePath = path.GetPath()
        String subFolder = path.CreateSubFolder("screenshots")
        boolean interrupted = false

        Date date =  new Date().format("yyyy-MM-dd_HH-mm-SS")
        def alert = com.eviware.soapui.support.UISupport
        Thread thread = new Thread()
        if (timer == 0 ) timer = 6
        try{
            alert.showInfoMessage("screenshot will be taken in $timer seconds")
            thread.sleep(Math.round(1*1000* timer))
            Rectangle rectumtangle =  new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            BufferedImage image = new Robot().createScreenCapture(rectumtangle)
            ImageIO.write(image, "png", new File("$subFolder/IMG$date.png"))
           //WINDOWS ONLY sound effect new entry can be created to cope with other  OS
            Runnable sound =
                    (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.default")
            if (sound != null) sound.run()


        }catch (InterruptedException | AWTException | IOException e){
            interrupted = true
            log.error(e.toString())

        }
        if(interrupted){
            Thread.currentThread().interrupt()
        }


    }
}
context.setProperty("screenshot", new ScreenSaver(log, context, TestRunner))