package com.miapp;

public class IP {
    private int[] octetos;

    public IP(int[] octetos) {
        this.octetos = octetos;
    }

    public IP(String ip) {
        this.octetos = parseIP(ip);
    }

    private int[] parseIP(String ip) {
        String[] partes = ip.split("\\.");
        if (partes.length != 4) throw new IllegalArgumentException("Formato de IP inválido: xxx.xxx.xxx.xxx");
        int[] octetos = new int[4];
        for(int i = 0; i < 4; i++) {
            octetos[i] = Integer.parseInt(partes[i]);
            if (octetos[i]<0 || octetos[i]>255){
                throw new IllegalArgumentException("Octeto Invalido: " + partes[i]);
            }
        }
        return octetos;
    }

    public IP incrementarIP(int cantidad) {
        int[] octetos = this.getOctetos();;
        int valor = cantidad;

        for (int i = octetos.length - 1; i >= 0; i--) {
            octetos[i] += valor;
            if (octetos[i] > 255) {
                valor = octetos[i] / 256;
                octetos[i] %= 256;
            } else {
                valor = 0;
                break;
            }
        }
        if (valor > 0) {
            throw new IllegalArgumentException("El incremento desborda el rango de direcciones IP.");
        }
        return new IP(octetos);
    }

    public int[] getOctetos() {
        return octetos.clone();
    }

    public String ipBinariaDecimal(int mascara){
        int n = 0;
        if(mascara<8){
            n=0;
        } else if(mascara<16){
            n=1;
        } else if(mascara<24){
            n=2;
        } else{
            n=3;
        }
        return this.toBinaryString(n) + "   "  + this.toString();
    }

    private String octecString(int octet) {
        String binary = Integer.toBinaryString(octet);
        return String.format("%8s", binary).replace(' ', '0');
    }

    public String toBinaryString(int n) {
        StringBuilder binaryIP = new StringBuilder();
        for (int i = 0; i < octetos.length; i++) {
            if(n>i){
                binaryIP.append(octetos[i]).append(".");
            } else {
                binaryIP.append(octecString(octetos[i]));
                if (i < octetos.length - 1) {
                    binaryIP.append(".");
                }
            }
        }
        return binaryIP.toString();
    }

    @Override
    public String toString() {
        return octetos[0] + "." + octetos[1] + "." + octetos[2] + "." + octetos[3];
    }
}