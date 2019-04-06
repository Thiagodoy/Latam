package com.core.behavior.services;

import com.core.behavior.model.Ticket;
import com.core.behavior.repository.TicketRepository;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class TicketService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DataSource dataSource;

    @Transactional
    public int[] saveBatch(List<Ticket> tickets) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.batchUpdate("INSERT INTO\n"
                + "    `behavior`.`ticket` (\n"
                + "        `ond_direcional`,\n"
                + "        `agencia_consolidada`,\n"
                + "        `ato_destino`,\n"
                + "        `ato_origem`,\n"
                + "        `base_tarifaria`,\n"
                + "        `base_venda`,\n"
                + "        `bilhete`,\n"
                + "        `cia_bilhete`,\n"
                + "        `cia_voo`,\n"
                + "        `classe_cabine`,\n"
                + "        `classe_servico`,\n"
                + "        `classe_tarifa`,\n"
                + "        `cliente_empresa`,\n"
                + "        `cnpj_cliente_empresa`,\n"
                + "        `cpf_pax`,\n"
                + "        `data_emissao`,\n"
                + "        `data_extracao`,\n"
                + "        `data_reserva`,\n"
                + "        `data_voo`,\n"
                + "        `digito_verificador_cc`,\n"
                + "        `familia_tarifaria`,\n"
                + "        `hora_emissao`,\n"
                + "        `hora_reserva`,\n"
                + "        `hora_voo`,\n"
                + "        `iata_agencia_emissora`,\n"
                + "        `nome_cliente`,\n"
                + "        `nome_pax`,\n"
                + "        `numero_cupom`,\n"
                + "        `num_voo`,\n"
                + "        `pnr_agencia`,\n"
                + "        `pnr_cia_aerea`,\n"
                + "        `qtde_pax`,\n"
                + "        `rt_ow`,\n"
                + "        `selfbooking_offline`,\n"
                + "        `tarifa_publica_us$`,\n"
                + "        `tarifa_publica_r$`,\n"
                + "        `tier_fidelidade_pax`,\n"
                + "        `tipo_pagamento`,\n"
                + "        `tipo_pax`,\n"
                + "        `tipo_venda`,\n"
                + "        `tour_code`,\n"
                + "        `trecho_tkt`,\n"
                + "        `valor_brl`,\n"
                + "        `valor_us$`,\n"
                + "        `created_at`,\n"
                + "        `file_id`,\n"
                + "        `version`\n"
                + "    )\n"
                + "VALUES\n"
                + "    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?,?, ?, ?, ?, ?,?,?)",
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, tickets.get(i).getOndDirecional());
                ps.setString(2, tickets.get(i).getAgenciaConsolidada());
                ps.setString(3, tickets.get(i).getAtoDestino());
                ps.setString(4, tickets.get(i).getAtoOrigem());
                ps.setString(5, tickets.get(i).getBaseTarifaria());
                ps.setString(6, tickets.get(i).getBaseVenda());
                ps.setLong(7, checkLong(tickets.get(i).getBilhete()));
                ps.setString(8, tickets.get(i).getCiaBilhete());
                ps.setString(9, tickets.get(i).getCiaVoo());
                ps.setString(10, tickets.get(i).getClasseCabine());
                ps.setString(11, tickets.get(i).getClasseServico());
                ps.setString(12, tickets.get(i).getClasseTarifa());
                ps.setString(13, tickets.get(i).getClienteEmpresa());
                ps.setString(14, tickets.get(i).getCnpjClienteEmpresa());
                ps.setLong(15, checkLong(tickets.get(i).getCpfPax()));
                ps.setDate(16, checDate(tickets.get(i).getDataEmissao()));
                ps.setDate(17, checDate(tickets.get(i).getDataExtracao()));
                ps.setDate(18, checDate(tickets.get(i).getDataReserva()));
                ps.setDate(19, checDate(tickets.get(i).getDataVoo()));
                ps.setLong(20, checkLong(tickets.get(i).getDigitoVerificadorCC()));
                ps.setString(21, tickets.get(i).getFamiliaTarifaria());
                ps.setString(22, tickets.get(i).getHoraEmissao());
                ps.setString(23, tickets.get(i).getHoraReserva());                
                ps.setString(24, tickets.get(i).getHoraVoo());
                ps.setLong(25, checkLong(tickets.get(i).getIataAgenciaEmissora()));
                ps.setString(26, tickets.get(i).getNomeCliente());
                ps.setString(27, tickets.get(i).getNomePax());
                ps.setLong(28, checkLong(tickets.get(i).getNroCupom()));
                ps.setLong(29, checkLong(tickets.get(i).getNumVoo()));
                ps.setString(30, tickets.get(i).getPnrAgencia());
                ps.setString(31, tickets.get(i).getPnrCiaArea());
                ps.setLong(32, checkLong(tickets.get(i).getQtdePax()));
                ps.setString(33, tickets.get(i).getRtOn());
                ps.setString(34, tickets.get(i).getSelfBookingOffiline());
                ps.setDouble(35, checkDouble(tickets.get(i).getTarifaPublicUs()));
                ps.setDouble(36, checkDouble(tickets.get(i).getTarifaPublica()));
                ps.setString(37, tickets.get(i).getTierFidelidadePax());
                ps.setString(38, tickets.get(i).getTipoPagamento());
                ps.setString(39, tickets.get(i).getTipoPax());
                ps.setString(40, tickets.get(i).getTipoVenda());
                ps.setString(41, tickets.get(i).getTourCode());
                ps.setString(42, tickets.get(i).getTrechoTkt());
                ps.setDouble(43, checkDouble(tickets.get(i).getValorBrl()));
                ps.setDouble(44, checkDouble(tickets.get(i).getValorUs()));
                ps.setDate(45, new Date(new java.util.Date().getTime()));
                ps.setLong(46, checkLong(tickets.get(i).getFileId()));
                ps.setLong(47, checkLong(tickets.get(i).getFileId()));
                 
                

            }

            @Override
            public int getBatchSize() {
                return 1000;
            }
        });

    }

    @Transactional
    public List<Ticket> saveAll(List<Ticket> list) {
        return ticketRepository.saveAll(list);
    }

    public long checkLong(Long value){
        return value != null ? value : 0;
    }
    
    public double checkDouble(Double value){
        return value != null ? value : 0.0;
    }
    
    public Date checDate(java.util.Date value){
        return value != null ? new Date(value.getTime()) : null;
    }

}
