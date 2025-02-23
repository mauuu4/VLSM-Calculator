package com.miapp;

import java.util.*;

public class CalculadoraVLSM {
    private IP ip;
    private int mascara;
    private int[] lanHosts;
    private Red redPadre;

    public CalculadoraVLSM(String ip, int mascara, int[] lanHosts) {
        this.ip = new IP(ip);
        this.mascara = mascara;
        this.lanHosts = lanHosts;
        this.redPadre = new Red(this.ip, mascara);
    }

    private Red encontrarRedDisponible(Red red) {
        if (red.estaDisponible()) {
            return red;
        }
        if (!red.subredes.isEmpty()) {
            for (Red subred : red.subredes) {
                Red disponible = encontrarRedDisponible(subred);
                if (disponible != null) {
                    return disponible;
                }
            }
        }
        return null;
    }

    public void calcular() {
        int cont = 1;
        sortHosts();

        for (int hosts : lanHosts) {

            Red redActual = encontrarRedDisponible(redPadre);

            if (redActual == null) {
                System.out.println("Error: No hay redes disponibles para la LAN");
                continue;
            }
            int bitsNecesarios = (int) Math.ceil(Math.log(hosts + 2) / Math.log(2));
            int nuevaMascara = 32 - bitsNecesarios;

            if (nuevaMascara > redActual.mascara) {
                List<IP> subredes = generarSubredes(redActual.ip, redActual.mascara, nuevaMascara);
                for (IP nuevaIp : subredes) {
                    redActual.agregarSubred(new Red(nuevaIp, nuevaMascara));
                }

                redActual = redActual.subredes.get(0);
            }
            redActual.lanAsignada = "" + cont++;

        }
    }

    private List<IP> generarSubredes(IP ip, int mascara, int nuevaMascara) {
        List<IP> redesGeneradas = new ArrayList<>();
        int bitsAgregados = nuevaMascara - mascara;
        int numSubredes = (int) Math.pow(2, bitsAgregados);
        int hosts = (int) Math.pow(2, 32 - nuevaMascara);

        IP ipInicial = ip;
        for (int i = 0; i < numSubredes; i++) {
            redesGeneradas.add(ipInicial);
            ipInicial = ipInicial.incrementarIP(hosts);
        }
        return redesGeneradas;
    }

    public void sortHosts() {
        Arrays.sort(lanHosts);
        for (int i = 0; i < lanHosts.length / 2; i++) {
            int temp = lanHosts[i];
            lanHosts[i] = lanHosts[lanHosts.length - 1 - i];
            lanHosts[lanHosts.length - 1 - i] = temp;
        }
    }

    public String mostrarResultados() {
        return redPadre.mostrar("");
    }
    public Red getRedPadre() {
        return redPadre;
    }

    @Override
    public String toString() {
        return redPadre.toString();
    }
}
