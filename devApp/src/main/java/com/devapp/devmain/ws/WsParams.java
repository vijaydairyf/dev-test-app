package com.devapp.devmain.ws;

/**
 * Created by x on 13/2/18.
 */

public class WsParams {
    private String model;
    private String prefix;
    private String suffix;
    private String separator;
    private String tareCommand;
    private String litreCommand;
    private String kgCommand;
    private int ignoreThreshold;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getTareCommand() {
        return tareCommand;
    }

    public void setTareCommand(String tareCommand) {
        this.tareCommand = tareCommand;
    }

    public String getLitreCommand() {
        return litreCommand;
    }

    public void setLitreCommand(String litreCommand) {
        this.litreCommand = litreCommand;
    }

    public String getKgCommand() {
        return kgCommand;
    }

    public void setKgCommand(String kgCommand) {
        this.kgCommand = kgCommand;
    }

    public int getIgnoreThreshold() {
        return ignoreThreshold;
    }

    public void setIgnoreThreshold(int ignoreThreshold) {
        this.ignoreThreshold = ignoreThreshold;
    }
}
