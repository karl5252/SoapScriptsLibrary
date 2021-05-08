class Scheduler {
    def log
    def context
    def testRunner

    Scheduler(logIn, contextIn, testRunnerIn){
        this.log = logIn
        this.context = contextIn
        this.testRunner = testRunnerIn
    }

    def testSchedule(def intervalCounter, def intervalLength = 1){
        def stepCount = testRunner.testCase.getTestStepCount()
        def type
        boolean interrupted = false
        Thread thread = new Thread()
        while(intervalCounter > 0){
            try{
                intervalCounter--
                for (def i = 0; i < stepCount;i++){
                    //actually below could be optional we could just run whole case over and over
                    type = context.testCase.getTestStepAt(i).config.type
                    if (type == 'request'){
                        def temporaryStep = context.testCase.getTestStepAti
                        temporaryStep.run(testRunner,context) //we run again the request step

                    }
                }
                thread.sleep(Math.round(intervalLength * 1000 * 60 * 60))
            }catch(InterruptedException e){
                log.info("Interruption... " + e)
                interrupted = true
            }
        }
        if (interrupted){
            log.info("Finished")
            Thread.currentThread().interrupt()
        }

    }
}
context.setProperty("schedule", new Scheduler(log, context, testRunner))