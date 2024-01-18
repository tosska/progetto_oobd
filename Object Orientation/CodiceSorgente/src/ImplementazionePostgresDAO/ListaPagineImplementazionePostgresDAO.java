package ImplementazionePostgresDAO;

import DAO.ListaPagineDAO;
import Database.ConnessioneDatabase;
import Model.Frase;

import java.sql.*;
import java.util.ArrayList;

public class ListaPagineImplementazionePostgresDAO implements ListaPagineDAO {
    private Connection connection;

    public ListaPagineImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPaginaDB(String titolo, Timestamp data, String autore) {
        try {
            PreparedStatement addPaginaPS = connection.prepareStatement("INSERT INTO pagina VALUES"
                    + "(default,'"+titolo+"','"+data+"', '"+autore+"')");
            addPaginaPS.executeUpdate();
            // connection.close();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }

    }

    @Override
    public int recuperaIdPagina() {
        int idPagina = 0;
        try {
            PreparedStatement queryControllo = connection.prepareStatement("SELECT MAX(id_pagina) FROM pagina");
            ResultSet rs = queryControllo.executeQuery();

            while (rs.next()) {
                idPagina = rs.getInt(1);
            }

            rs.close();
            queryControllo.close();
            // connection.close();


        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
        return idPagina;
    }

    @Override
    public void addFraseDB(int idPagina, ArrayList<Frase> listaFrasi) {
        for (Frase f: listaFrasi)
        {
            try {
                PreparedStatement addFrasePS = connection.prepareStatement("INSERT INTO frase VALUES"
                        + "(default,'"+idPagina+"','"+f.getContenuto()+"', '"+f.getRiga()+"', '"+false+"')");
                addFrasePS.executeUpdate();
                // connection.close();
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }

    }
}
