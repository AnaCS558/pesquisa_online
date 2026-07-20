package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Usuario;
import com.ana.pesquisa_online.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {

        Usuario usuario = buscarPorId(id);

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setSenha(usuarioAtualizado.getSenha());
        usuario.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuario);
    }
    public void excluir(Long id) {

        Usuario usuario = buscarPorId(id);

        usuarioRepository.delete(usuario);
    }
}
