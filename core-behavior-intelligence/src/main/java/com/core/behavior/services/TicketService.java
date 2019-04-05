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
                ps.setString(2, tickets.get(i).getOndDirecional());
                ps.setString(3, tickets.get(i).getAgenciaConsolidada());
                ps.setString(1, tickets.get(i).getAtoDestino());
                ps.setString(4, tickets.get(i).getAtoOrigem());
                ps.setString(2, tickets.get(i).getBaseTarifaria());
                ps.setString(3, tickets.get(i).getBaseVenda());
                ps.setLong(4, tickets.get(i).getBilhete());
                ps.setString(3, tickets.get(i).getCiaBilhete());
                ps.setString(4, tickets.get(i).getCiaVoo());
                ps.setString(1, tickets.get(i).getClasseCabine());
                ps.setString(2, tickets.get(i).getClasseServico());
                ps.setString(3, tickets.get(i).getClasseTarifa());
                ps.setString(4, tickets.get(i).getClienteEmpresa());
                ps.setString(1, tickets.get(i).getCnpjClienteEmpresa());
                ps.setLong(2, tickets.get(i).getCpfPax());
                ps.setDate(3, new Date(tickets.get(i).getDataEmissao().getTime()));
                ps.setDate(4, new Date(tickets.get(i).getDataExtracao().getTime()));
                ps.setDate(3, new Date(tickets.get(i).getDataReserva().getTime()));
                ps.setDate(4, new Date(tickets.get(i).getDataVoo().getTime()));
                ps.setLong(1, tickets.get(i).getDigitoVerificadorCC());
                ps.setString(2, tickets.get(i).getFamiliaTarifaria());
                ps.setString(3, tickets.get(i).getHoraEmissao());
                ps.setString(4, tickets.get(i).getHoraReserva());
                ps.setString(1, tickets.get(i).getHoraEmissao());
               // ps.setString(2, tickets.get(i).getFirstName());
               // ps.setString(3, tickets.get(i).getLastName());
               // ps.setString(4, tickets.get(i).getAddress();
               // ps.setString(3, tickets.get(i).getLastName());
                //ps.setString(4, tickets.get(i).getAddress();
                //ps.setInt(1, tickets.get(i).getId());
               /// ps.setString(2, tickets.get(i).getFirstName());
               // ps.setString(3, tickets.get(i).getLastName());
               // ps.setString(4, tickets.get(i).getAddress();
               // ps.setInt(1, tickets.get(i).getId());
               // ps.setString(2, tickets.get(i).getFirstName());
                //ps.setString(3, tickets.get(i).getLastName());

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

    @Transactional
    public <T extends Ticket> List<T> bulkSave(Collection<T> entities) {
        final List<T> savednotEntities = new ArrayList<T>(entities.size());
        int i = 0;
        for (T t : entities) {
            try {
                entityManager.persist(t);

            } catch (Exception e) {

                savednotEntities.add(t);
            }
            i++;
            if (i % 1000 == 0) {
                // Flush a batch of inserts and release memory.
                entityManager.flush();
                entityManager.clear();
            }
        }

        System.out.println("registros repetidos ->" + savednotEntities.size());

        return savednotEntities;
    }

    private <T extends Ticket> T persistOrMerge(T t) {

        try {
            entityManager.persist(t);
            return t;
        } catch (Exception e) {

            return null;
        }
//        if (true) {
//            entityManager.persist(t);
//            return t;
//        } else {
//            return entityManager.merge(t);
//        }

    }

}
