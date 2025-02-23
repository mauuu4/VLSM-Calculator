package com.miapp;

import java.util.ArrayList;
import java.util.List;

public class Red {
    IP ip;
    int mascara;
    String lanAsignada;
    List<Red> subredes;

    public Red(IP ip, int mascara) {
        this.ip = ip;
        this.mascara = mascara;
        this.subredes = new ArrayList<>();
    }

    public void agregarSubred(Red subred) {
        subredes.add(subred);
    }

    public boolean estaDisponible(){
        return this.lanAsignada == null && this.subredes.isEmpty();
    }

    public IP getGateway() {
        return ip.incrementarIP(1);
    }

    public IP getPrimerHost() {
        return ip.incrementarIP(2);
    }

    public IP getUltimoHost() {
        return ip.incrementarIP(hosts());
    }

    public IP getBroadcast() {
        return ip.incrementarIP(hosts()+1);
    }

    public int hosts(){
        return (int) Math.pow(2, 32 - mascara)-2;
    }

    public String mostrar(String prefijo) {
        String s = "";
        String asignacion = lanAsignada != null ? " --> LAN " + lanAsignada : "";
        s+=prefijo+ ip.ipBinariaDecimal(mascara) + "/" + mascara + asignacion;
        s+= "\n";

        if (subredes.size() > 20) {
            // Mostrar las primeras 10 subredes
            for (int i = 0; i < 10; i++) {
                s+= subredes.get(i).mostrar(prefijo + "    ");
            }

            // Mostrar indicador de subredes omitidas
            s+= prefijo + "    (...se omitieron " + (subredes.size() - 20) + " subredes...)";
            s+="\n";

            // Mostrar las últimas 10 subredes
            for (int i = subredes.size() - 10; i < subredes.size(); i++) {
                s+=subredes.get(i).mostrar(prefijo + "    ");
            }
        } else {
            // Si hay 20 o menos subredes, mostrar todas
            for (Red subred : subredes) {
                s+=subred.mostrar(prefijo + "    ");
            }
        }
        return s;
    }

    public String getInfoTabla() {
        StringBuilder s = new StringBuilder();
        if (lanAsignada != null) {
            s.append(ip).append("/").append(mascara).append("|")  // Red
                    .append(getGateway()).append("|")                    // Gateway
                    .append(getPrimerHost()).append("|")                 // Primer Host
                    .append(getUltimoHost()).append("|")                 // Último Host
                    .append(getBroadcast()).append("|")                  // Broadcast
                    .append("LAN ").append(lanAsignada);                 // LAN
            s.append("\n");
        }
        for (Red subred : subredes) {
            s.append(subred.getInfoTabla());
        }
        return s.toString();
    }

    @Override
    public String toString() {
        String s = "";
        if (lanAsignada != null) {
            s += "Red:         " +  ip + "/" + mascara +"\n" +
                    "Gateway:     " + getGateway() + "\n" +
                    "Primer Host: " + getPrimerHost() + "\n" +
                    "Último Host: " + getUltimoHost() + "\n" +
                    "Broadcast:   " + getBroadcast() + "\n" + "\n";
        }
        for (Red subred :subredes) {
            s+= subred.toString();
        }
        return s;
    }
}
