/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.malyskok.rsocket.dto;

public class ChartResponseDto {

    private int input;
    private int output;

    public ChartResponseDto() {
    }

    public ChartResponseDto(int input, int output) {
        this.input = input;
        this.output = output;
    }

    public int getInput() {
        return input;
    }

    public int getOutput() {
        return output;
    }

    @Override
    public String toString() {
        String graphFormat = getFormat(this.output);
        return String.format(graphFormat, this.input, "X");
    }

    private String getFormat(int value){
        return "%3s|%" + value + "s";
    }
}