package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;

import java.util.List;

public class InterfazXML implements InterfazDAO {

    @Override
    public boolean lleno() {
        return false;
    }

    @Override
    public boolean vacio() {
        return false;
    }

    @Override
    public boolean insertar(Object obj) throws InsercionException, DataFullException, DuplicateEntry {

        return false;
    }

    @Override
    public boolean eliminar(Object obj) throws ObjectNotExist, DataEmptyAccess {
        return false;
    }

    @Override
    public boolean actualizar(Object obj) throws ObjectNotExist, DataEmptyAccess {
        return false;
    }

    @Override
    public List<Usuario> buscar() throws DataEmptyAccess {
        return null;
    }

    @Override
    public void buscarAtributo(String atributo) throws DataEmptyAccess {

    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, DuplicateEntry {
        return false;
    }

    @Override
    public List<Object> getTopFive(Object obj) throws DataEmptyAccess {
        return List.of();
    }

    @Override
    public String mejorTiempo() {
        return "";
    }

}