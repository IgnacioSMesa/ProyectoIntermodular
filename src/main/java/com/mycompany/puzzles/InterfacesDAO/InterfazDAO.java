package com.mycompany.puzzles.InterfacesDAO;

import com.mycompany.puzzles.Clases.Usuario;
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
     * @param obj de tipo puzzle o usuario
     * @return true si se inserta o false si no se inserta
     * @throws InsercionException
     * @throws DataFullException
     * @throws DuplicateEntry
     */
    public boolean insertar(Object obj)throws InsercionException, DataFullException, DuplicateEntry;

    /**
     *
     * @param email objeto a eliminar
     * @return true si se elimina false si no se elimina
     * @throws ObjectNotExist si no existe el objeto
     * @throws DataEmptyAccess si esta vacio
     */
    public boolean eliminar(String email) throws DataEmptyAccess, DeleteException, DataAccessException, ObjectNotExist;

    /**
     *
     * @param obj objeto a actualizar
     * @return true si se actualiza false si no se actualiza
     * @throws ObjectNotExist
     * @throws DataEmptyAccess
     */
    public boolean actualizar(Object obj) throws  ObjectNotExist, DataEmptyAccess;

    /**
     * Devuelve un listado de los objetos
     * @throws DataEmptyAccess
     */
    public List<Usuario> buscar() throws DataEmptyAccess;

    /**
     * Devuelve lo que queramos buscar por el argumento
     * @param atributo por el que buscar
     * @throws DataEmptyAccess
     */
    public List<String> buscarAtributo(String atributo) throws  DataEmptyAccess;

    /**
     *
     * @param obj de tipo usuario
     * @return true si el usuario esta bloqueado false si no esta bloqueado
     * @throws DataFullException
     * @throws DuplicateEntry
     */
    public boolean bloquearUsuario(Object obj) throws DataFullException, DuplicateEntry, InsercionException;

    /**
     *
     * @return una lista de los 5 puzzles mejor valorados del usuario
     * @throws DataEmptyAccess
     */
    public List<Object> getTopFive(Object obj) throws DataEmptyAccess;

    public String mejorTiempo();

}