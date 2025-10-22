/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puzzles.InterfacesDAO;

import com.mycompany.puzzles.Excecpiones.*;

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
     * @param obj de tipo puzzle o usuario
     * @return true si se inserta o false si no se inserta
     * @throws InsercionException
     * @throws DataFullException
     * @throws DuplicateEntry
     */
    public boolean insertar(Object obj)throws InsercionException, DataFullException, DuplicateEntry;

    /**
     *
     * @param obj de tipo objeto puzzle o usuario
     * @return true si se elimina false si no se elimina
     * @throws ObjectNotExist si no existe el objeto
     * @throws DataEmptyAccess si esta vacio
     */
    public boolean eliminar(Object obj) throws ObjectNotExist, DataEmptyAccess;

    /**
     *
     * @param obj de tipo puzzle o usuario
     * @return true si se actualiza false si no se actualiza
     * @throws ObjectNotExist
     * @throws DataEmptyAccess
     */
    public boolean actualizar(Object obj) throws  ObjectNotExist, DataEmptyAccess;

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

}