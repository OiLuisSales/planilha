/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Codigo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author OI324558
 */
public class MatrizDinamica<T> {

    private final Map<Integer, Map<Integer, T>> elementos = new HashMap<Integer, Map<Integer, T>>();

    public void set(int linha, int coluna, T elemento) {
        Map<Integer, T> colunas = getColunas(linha);
        Integer chave = Integer.valueOf(coluna);
        if (elemento != null) {
            colunas.put(chave, elemento);
        } else {
            colunas.remove(chave);
        }
    }

    public T get(int linha, int coluna) {
        Map<Integer, T> colunas = getColunas(linha);
        Integer chave = Integer.valueOf(coluna);
        T elemento = colunas.get(chave);
        return elemento;
    }

    private Map<Integer, T> getColunas(int linha) {
        int chave = Integer.valueOf(linha);
        Map<Integer, T> colunas = elementos.get(chave);
        if (colunas == null) {
            colunas = new HashMap<Integer, T>();
            elementos.put(chave, colunas);
        }
        return colunas;
    }

    public int getSizeX() {
        return elementos.size();
    }

    public int getSizeY(int x) {
        Map<Integer, T> colunas = getColunas(x);
        return colunas.size();
    }
}
