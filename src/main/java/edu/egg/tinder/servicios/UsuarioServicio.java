
package edu.egg.tinder.servicios;

import edu.egg.tinder.entidades.Foto;
import edu.egg.tinder.entidades.Usuario;
import edu.egg.tinder.entidades.Zona;
import edu.egg.tinder.errores.ErrorServicio;
import edu.egg.tinder.repositorios.UsuarioRepositorio;
import edu.egg.tinder.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService{
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private ZonaRepositorio zonaRepositorio;
    
    @Autowired
    private FotoServicio fotoServicio;
    
    @Autowired
    private NotificacionServicio notificacionServicio;
    
    @Transactional
    public void registrar(MultipartFile archivo,String nombre,String apellido, String mail,String clave, String clave2,String idZona) throws ErrorServicio{
        Zona zona=zonaRepositorio.getOne(idZona);
        
        validar(nombre, apellido, mail, clave, clave2,zona);
 
        Usuario usuario=new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setZona(zona);
        
        String encriptada=new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);
        
        usuario.setAlta(new Date());
        
        Foto foto=fotoServicio.guardar(archivo);
        usuario.setFoto(foto);
        
        usuarioRepositorio.save(usuario);
        
//        try {
//            notificacionServicio.enviar("Bienvenido al Tinder de Mascota", "Tinder de Mascota", usuario.getMail());
//        } catch (MessagingException ex) {
//            System.out.println("error enviar mensaje");
//        }
    }
    
    @Transactional
    public void modificar(MultipartFile archivo,String id,String nombre, String apellido, String mail,String clave, String clave2, String idZona) throws ErrorServicio{
        Zona zona=zonaRepositorio.getOne(idZona);
        
        validar(nombre, apellido, mail, clave,clave2,zona);
        Optional<Usuario> respuesta=usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setApellido(apellido);
            usuario.setNombre(nombre);
            usuario.setMail(mail);
            usuario.setZona(zona);
            
            String encriptada=new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);
            
            String idFoto=null;
            if (usuario.getFoto()!=null) {
                idFoto=usuario.getFoto().getId();
            }
            Foto foto=fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);
            
            usuarioRepositorio.save(usuario);
        }else{
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }
    
    @Transactional
    public void desahabilitar(String id) throws ErrorServicio{
        Optional<Usuario> respuesta=usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        }else{
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }
    
    @Transactional
    public void habilitar(String id) throws ErrorServicio{
        Optional<Usuario> respuesta=usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);
        }else{
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }
    
    public Usuario buscarPorId(String id) throws ErrorServicio{
        Optional<Usuario> respuesta=usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            return usuario;
        }else{
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }
    private void validar(String nombre,String apellido, String mail,String clave, String clave2, Zona zona) throws ErrorServicio{
        if (nombre==null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no debe estar vacio");
        }
        if (apellido==null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido no debe estar vacio");
        }
        if (mail==null || mail.isEmpty()) {
            throw new ErrorServicio("El mail no debe estar vacio");
        }
        if (clave==null || clave.isEmpty() || clave.length()<=6) {
            throw new ErrorServicio("El clave no debe estar vacia y debe tener mas de 6 digitos");
        }
        
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las claves no coinciden");
        }
        
        if (zona==null) {
            throw new ErrorServicio("No se encontró la zona solicitada");
        }
    }
    //ES PARA EL LOGIN IDENTIFICARSE NECESITA UN IMPLEMENTS ARRIBA
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario=usuarioRepositorio.buscarPorMail(mail);
       
        if (usuario!=null) {
            
              List<GrantedAuthority> permisos=new ArrayList<>();
              GrantedAuthority p1=new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
              permisos.add(p1);
              
              ServletRequestAttributes attr=(ServletRequestAttributes)RequestContextHolder.currentRequestAttributes(); 
              HttpSession session=attr.getRequest().getSession(true);
              for (Enumeration<String> e = attr.getRequest().getParameterNames(); e.hasMoreElements();)
                System.out.println(e.nextElement());
              System.out.println("-----------------"+(attr.getRequest().getParameterValues("username"))[0]);
              session.setAttribute("usuariosession", usuario);
//            GrantedAuthority p1=new SimpleGrantedAuthority("MODULO_FOTOS");
//            permisos.add(p1);
//            
//            GrantedAuthority p2=new SimpleGrantedAuthority("MODULO_MASCOTAS");
//            permisos.add(p2);
//            
//            GrantedAuthority p3=new SimpleGrantedAuthority("MODULO_VOTOS");
//            permisos.add(p3);
            
            User user=new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        }else{
            return null;
        }
    }
}   
