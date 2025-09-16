package ru.otus;

public class Main {
    public static void main(String[] args) {
        NumberSequence sequence = new NumberSequence(1, 10, 500);
        Runner runner = new Runner(sequence);
        runner.run();
    }
}
