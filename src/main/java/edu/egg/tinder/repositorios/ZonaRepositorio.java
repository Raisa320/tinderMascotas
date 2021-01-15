/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.egg.tinder.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.egg.tinder.entidades.Zona;
/**
 *
 * @author CARMEN
 */
@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, String>{
    
}
