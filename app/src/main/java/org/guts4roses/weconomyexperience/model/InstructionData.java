package org.guts4roses.weconomyexperience.model;

import com.google.firebase.database.PropertyName;
import org.guts4roses.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 1-8-17.
 */

@Parcel
public class InstructionData extends FireData {

    @PropertyName("text")
    protected String text;

    @PropertyName("input")
    protected int input;

    @PropertyName("output")
    protected int output;

    @PropertyName("setLabour")
    protected int labour;

    @PropertyName("input_type")
    protected String inputType;

    @PropertyName("output_type")
    protected String outputType;

    public InstructionData() {
    }

    public InstructionData(String text) {
        this.text = text;
    }

    @PropertyName("text")
    public String getText() {
        return text;
    }

    @PropertyName("text")
    public void setText(String text) {
        this.text = text;
    }

    @PropertyName("input")
    public int getInput() {
        return input;
    }

    @PropertyName("input")
    public void setInput(int input) {
        this.input = input;
    }

    @PropertyName("output")
    public int getOutput() {
        return output;
    }

    @PropertyName("output")
    public void setOutput(int output) {
        this.output = output;
    }

    @PropertyName("labour")
    public int getLabour() {
        return labour;
    }

    @PropertyName("labour")
    public void setLabour(int labour) {
        this.labour = labour;
    }

    @PropertyName("input_type")
    public String getInputType() {
        return inputType;
    }

    @PropertyName("input_type")
    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    @PropertyName("output_type")
    public String getOutputType() {
        return outputType;
    }

    @PropertyName("output_type")
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }
}
