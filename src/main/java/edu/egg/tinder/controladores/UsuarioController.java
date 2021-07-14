/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.egg.tinder.controladores;

import edu.egg.tinder.entidades.Usuario;
import edu.egg.tinder.entidades.Zona;
import edu.egg.tinder.errores.ErrorServicio;
import edu.egg.tinder.repositorios.UsuarioRepositorio;
import edu.egg.tinder.repositorios.ZonaRepositorio;
import edu.egg.tinder.servicios.UsuarioServicio;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author CARMEN
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private ZonaRepositorio zonaRepositorio;
    
    @Autowired
    private UsuarioRepositorio uRepo;
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session,@RequestParam String id, ModelMap model){
        List<Zona> zonas=zonaRepositorio.findAll();
        model.put("zonas", zonas);
        Usuario login=(Usuario) session.getAttribute("usuariosession");
        if (login==null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        try {
            Usuario usuario=usuarioServicio.buscarPorId(id);
            model.addAttribute("perfil",usuario);
            throw new  ErrorServicio(id);
        } catch (ErrorServicio e) {
            model.addAttribute("error",e.getMessage());
            Calendar tiempo=Calendar.getInstance();
            tiempo.add(Calendar.MINUTE, 15);
            System.out.println("tambien aqui"+tiempo);
        }
        return "perfil.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String registrar(ModelMap modelo,HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam String clave1,@RequestParam String clave2,@RequestParam String idZona){
        Usuario usuario=null;
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }
            usuario=usuarioServicio.buscarPorId(id);
            usuarioServicio.modificar(archivo, id, nombre, apellido, mail, clave1, clave2, idZona);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/inicio";
        } catch (ErrorServicio e) {
            List<Zona> zonas=zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error",e.getMessage());
            modelo.put("perfil", usuario);
            return "perfil.html";
        }
    }
    
//    @RequestMapping(value = "/ubicacionesActivas", method = RequestMethod.GET)
//    @ResponseBody
//    public List<Usuario> nombreDuplicado(){
//        for (Usuario user : uRepo.findAll()) {
//            
//        }
//        return "";
//    }
}
