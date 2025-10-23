/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puzzles.InterfacesDAO;

import com.mycompany.puzzles.Excecpiones.*;

import java.util.List;

/**
 *
 * @author Ignacio y Natalia
 */
public interface InterfazDAO {

    /**
     *
     * @return true si está lleno false si hay espacio
     */
    public boolean lleno();

    /**
     *
     * @return true si está vacío
     */
    public boolean vacio();

    /**
     *
     * @param jsonData de tipo puzzle o usuario
     * @return true si se inserta o false si no se inserta
     * @throws InsercionException
     * @throws DataFullException
     * @throws DuplicateEntry
     */
    public boolean insertar(String jsonData)throws InsercionException, DataFullException, DuplicateEntry;

    /**
     *
     * @param jsonData objeto a eliminar
     * @return true si se elimina false si no se elimina
     * @throws ObjectNotExist si no existe el objeto
     * @throws DataEmptyAccess si esta vacio
     */
    public boolean eliminar(String jsonData) throws ObjectNotExist, DataEmptyAccess;

    /**
     *
     * @param jsonData objeto a actualizar
     * @return true si se actualiza false si no se actualiza
     * @throws ObjectNotExist
     * @throws DataEmptyAccess
     */
    public boolean actualizar(String jsonData) throws  ObjectNotExist, DataEmptyAccess;

    /**
     * Devuelve un listado de los objetos
     * @throws DataEmptyAccess
     */
    public void buscar() throws DataEmptyAccess;

    /**
     * Devuelve un objeto
     * @param atributo por el que buscar
     * @throws DataEmptyAccess
     */
    public void buscarAtributo(String atributo) throws  DataEmptyAccess;

    /**
     *
     * @param obj de tipo usuario
     * @return true si el usuario esta bloqueado false si no esta bloqueado
     * @throws DataFullException
     * @throws DuplicateEntry
     */
    public boolean bloquearUsuario(Object obj) throws DataFullException, DuplicateEntry;

    /**
     *
     * @return una lista de los 5 puzzles mejor valorados del usuario
     * @throws DataEmptyAccess
     */
    public List<Object> getTopFive(Object obj) throws DataEmptyAccess;

    public String mejorTiempo();

}