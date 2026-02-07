package com.viacep;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
    public static void main(String[] args) {
        
        Scanner leitura = new Scanner(System.in);

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        int choice = 0;

        do{

            String json = null;

            System.out.println("========SISTEMA VIACEP=========");
            System.out.println("1- Para consultar por CEP");
            System.out.println("2- Para consultar por cidade e logradouro");
            System.out.println("3- Para sair");
            System.out.println("===============================");
            System.out.println("Escolha uma opção: ");

            choice = leitura.nextInt();
            leitura.nextLine();

            switch(choice){

                case 1:{

                    try{

                        System.out.println("Informe seu CEP:");
                        String cepRecebido = leitura.nextLine().trim();

                        String cepLimpo = cepRecebido.replaceAll("\\D", "");

                        if (cepLimpo.length() != 8) {
                            
                            System.out.println("CEP inválido. Digite um CEP de oito dígitos");
                            break;
                        }

                        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://viacep.com.br/ws/" + cepRecebido +"/json/")).build();
                        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                        json = response.body();

                        Residencia residencia = gson.fromJson(json, Residencia.class);

                        System.out.println(residencia);

                    }catch(IOException e){

                        System.out.println("Erro de conexão: " + e.getMessage());
                    }catch(InterruptedException e){

                        System.out.println("Erro de requisição: " + e.getMessage());
                    }

                    break;
                }    
                case 2:{
                        try{

                        System.out.println("Informe a sigla do seu estado: ");
                        String ufRecebido = leitura.nextLine().trim().toUpperCase();
                        if (ufRecebido.length() != 2) {

                            System.out.println("Sigla inválida");
                            break;
                        }

                        System.out.println("Informe sua cidade: ");
                        String cidadeRecebida = leitura.nextLine().trim();

                        System.out.println("Informe seu logradouro");
                        String logradouroRecebido = leitura.nextLine().trim();

                        String cidadeCodificada = URLEncoder.encode(cidadeRecebida, StandardCharsets.UTF_8);

                        String logradouroCodificado = URLEncoder.encode(logradouroRecebido, StandardCharsets.UTF_8);

                        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://viacep.com.br/ws/" + ufRecebido + "/" + cidadeCodificada + "/" + logradouroCodificado + "/json/")).build();
                        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                        json = response.body();

                        if (json.contains("\"erro\": true")) {
                            System.out.println("CEP não encontrado!");
                            break;

                        }

                        List<CEP> listaResidencias = Arrays.asList(gson.fromJson(json, CEP[].class));

                        if (listaResidencias.isEmpty()) {
                            
                            System.out.println("Nenhum endereço encontrado");
                        }else{
                            for (CEP cep : listaResidencias){

                                System.out.println(cep);
                            }

                        }

                    }catch(IOException e){

                        System.out.println("Erro de conexão: " + e.getMessage());
                    }catch(InterruptedException e){

                        System.out.println("Erro de requisição: " + e.getMessage());
                    }
                    break;
                }
                case 3:
                    System.out.println("Encerrando programa...");
                    break;
                    
                default:
                    System.out.println("Opção inválida!");

            }        

        }while (choice != 3);

        leitura.close();
    }
}