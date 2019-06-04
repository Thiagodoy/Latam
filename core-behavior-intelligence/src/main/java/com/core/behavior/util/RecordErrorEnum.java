/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;



/**
 *
 * @author thiag
 */

public enum RecordErrorEnum {
 
DATAEMISSAO("Data da emissão inválida."),
DATAVOO("Data do voo inválida. Ou a data do voo ultrapassou os 330 dias ápos a data de emissão."),
HORAVOO("Hora do voo invalida.") ,
CIABILHETE("Codigo padrão da agencia não encontrado."),
TRECHOTKT("Itinerário não  consistente com o ato origem e destino."),
ATOORIGEM("Código padrão do aeroporto de origem inválido."),
ATODESTINO("Código padrão do aeroporto de destino inválido."),
NUMEROCUPOM("Numero do cupom invalido."),
BILHETE("Numero do bilhete inválido."),
TIPOVENDA("Tipo de venda inválido."),
CLASSECABINE("Classe da Cabine inválida."),
CIAVOO("Código da companhia inválida."),
VALORBRL("Valor inválido."),
CLIENTEEMPRESA("Cliente empresa inválida"),
CNPJCLIENTEEMPRESA("Cnpj do cliente empresa inválida."),
IATAAGENCIAEMISSORA("Codigo da agencia emissora inválida."),
BASEVENDA("Base da venda inválida."),
QTDPAX("Quantidade de passageiros inválida."),
NUMVOO("Numero do vou inválida."),
AGENCIACONSOLIDADA("Agencia consolidada invalida.");  
   
public String message;
    
    
    RecordErrorEnum(String message){
        this.message = message;
    }
    
}
