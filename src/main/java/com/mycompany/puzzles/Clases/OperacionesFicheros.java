package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.CreacionFicheroException;
import com.mycompany.puzzles.Excecpiones.EliminarFicheroException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class OperacionesFicheros {

    private Path ficheroJSONOrigen = Paths.get("src/main/resources/Ficheros/usuarios.json");
    private Path ficheroBloqJSONOrigen = Paths.get("src/main/resources/Ficheros/usuariosBloqueados.json");
    private Path ficheroXMLOrigen = Paths.get("src/main/resources/Ficheros/usuarios.xml");
    private Path ficheroBloqXMLOrigen = Paths.get("src/main/resources/Ficheros/usuariosBloqueados.xml");

    private Path ficheroJSONDestino = Paths.get("src/main/resources/CopiasSeguridad/usuarios.json");
    private Path ficheroBloqJSONDestino = Paths.get("src/main/resources/CopiasSeguridad/usuariosBloqueados.json");
    private Path ficheroXMLDestino = Paths.get("src/main/resources/CopiasSeguridad/usuarios.xml");
    private Path ficheroBloqXMLDestino = Paths.get("src/main/resources/CopiasSeguridad/usuariosBloqueados.xml");

    public void copiarFicheros() throws CreacionFicheroException {

        if(!Files.exists(ficheroJSONDestino)) {
            try{
                Files.createFile(ficheroJSONDestino);
            }catch(IOException e){
                throw new CreacionFicheroException("Error al crear el fichero" + e);
            }
        }
        if(!Files.exists(ficheroBloqJSONDestino)) {
            try{
              Files.createFile(ficheroBloqJSONDestino);
            }catch(IOException e){
                throw new CreacionFicheroException("Error al crear el fichero" + e);
            }
        }
        if(!Files.exists(ficheroXMLDestino)) {
            try{
                Files.createFile(ficheroXMLDestino);
            }catch(IOException e){
                throw new CreacionFicheroException("Error al crear el fichero" + e);
            }
        }
        if(!Files.exists(ficheroBloqXMLDestino)) {
            try{
                Files.createFile(ficheroBloqXMLDestino);
            }catch(IOException e){
                throw new CreacionFicheroException("Error al crear el fichero" + e);
            }
        }

        try {
            // REPLACE_EXISTING → si ya existe, lo sobreescribe
            Files.copy(ficheroJSONOrigen, ficheroJSONDestino, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(ficheroBloqJSONOrigen, ficheroBloqJSONDestino, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(ficheroXMLOrigen, ficheroXMLDestino, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(ficheroBloqXMLOrigen, ficheroBloqXMLDestino, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ Archivo copiado correctamente.");
        } catch (IOException e) {
            System.out.println("❌ Error al copiar el archivo: " + e.getMessage());
        }
    }
    public void eliminarFicheros() throws EliminarFicheroException {

        try {

            if (Files.exists(ficheroJSONDestino)) {
                Files.delete(ficheroJSONDestino);
            }
            if (Files.exists(ficheroBloqJSONDestino)) {
                Files.delete(ficheroBloqJSONDestino);
            }
            if (Files.exists(ficheroXMLDestino)) {
                Files.delete(ficheroXMLDestino);
            }
            if (Files.exists(ficheroBloqXMLDestino)) {
                Files.delete(ficheroBloqXMLDestino);
            }

        } catch (IOException e) {
            throw new EliminarFicheroException("Error al eliminar el fichero" + e);
        }
    }
}
