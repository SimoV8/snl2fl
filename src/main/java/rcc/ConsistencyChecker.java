package rcc;

import rcc.mc.ModelChecker;
import snl2fl.Snl2FlParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ConsistencyChecker {

    public enum Result {
        CONSISTENT,
        INCONSISTENT,
        FAIL
    }

    private ModelChecker mc;
    private Snl2FlParser parser;
    private String outputFilePath;

    public ConsistencyChecker(ModelChecker mc, Snl2FlParser parser, String outputFilePath) {
        this.mc = mc;
        this.parser = parser;
        this.outputFilePath = outputFilePath;
    }

    public ModelChecker getMc() {
        return mc;
    }

    public void setMc(ModelChecker mc) {
        this.mc = mc;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public Snl2FlParser getParser() {
        return parser;
    }

    public void setParser(Snl2FlParser parser) {
        this.parser = parser;
    }

    public Result run() {

        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            parser.translate(mc.getTranslator(), fos);
            fos.close();
            ModelChecker.Result res = mc.run(outputFilePath);

            switch(res) {
                case SAT:
                    return Result.CONSISTENT;
                case UNSAT:
                    return Result.INCONSISTENT;
                case FAIL:
                    return Result.FAIL;
            }

        } catch(IOException e) { e.printStackTrace(); }

        return Result.FAIL;
    }

    public ExecutorService runAsync(Consumer<Result> consumer) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> consumer.accept(run()));
        return executor;
    }




}
