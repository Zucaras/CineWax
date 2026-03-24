package com.waxeados.CineWax.mappers;

import com.waxeados.CineWax.dto.EstadoDTO;
import com.waxeados.CineWax.dto.GeneroDTO;
import com.waxeados.CineWax.dto.MunicipioDTO;
import com.waxeados.CineWax.dto.SalaDTO;
import com.waxeados.CineWax.entity.Estado;
import com.waxeados.CineWax.entity.Genero;
import com.waxeados.CineWax.entity.Municipio;
import com.waxeados.CineWax.entity.Sala;
import org.springframework.stereotype.Component;

@Component
public class CatalogoMapper {

    public EstadoDTO toEstadoDTO(Estado e) {
        return EstadoDTO.builder()
                .idEstado(e.getIdEstado())
                .nombreEstado(e.getNombreEstado())
                .build();
    }

    public MunicipioDTO toMunicipioDTO(Municipio m) {
        return MunicipioDTO.builder()
                .idMunicipio(m.getIdMunicipio())
                .letraMunicipio(m.getLetraMunicipio().toString())
                .nombreMunicipio(m.getNombreMunicipio())
                .build();
    }

    public GeneroDTO toGeneroDTO(Genero g) {
        return GeneroDTO.builder()
                .idGenero(g.getIdGenero())
                .nombreGenero(g.getNombreGenero())
                .build();
    }

    public SalaDTO toSalaDTO(Sala s) {
        return SalaDTO.builder()
                .idSala(s.getIdSala())
                .numeroSala(s.getNumeroSala())
                .municipio(s.getMunicipio().getNombreMunicipio())
                .build();
    }
}