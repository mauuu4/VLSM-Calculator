package com.miapp;

public class Main {
    public static void main(String[] args) {
        int[] lanHosts = {200000,130000,65536,30000,16384,4096,2046,1024,2,2,2, 8000};
        CalculadoraVLSM calc = new CalculadoraVLSM("170.144.0.0", 12, lanHosts);
        calc.calcular();
        System.out.println(calc);
        calc.mostrarResultados();
    }
}
