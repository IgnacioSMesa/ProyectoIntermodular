package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;
import com.mycompany.puzzles.Puzzles;

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
    public boolean eliminar(String email) throws DataEmptyAccess, DeleteException, DataAccessException{
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
    public List<String> buscarAtributo(String atributo) throws DataEmptyAccess {
        return List.of();
    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, DuplicateEntry {
        return false;
    }

    @Override
    public Puzzle[] getTopFive() throws DataEmptyAccess {
        return null;
    }

    @Override
    public String mejorTiempo() {
        return "";
    }

}