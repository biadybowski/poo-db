/**
* Para rodar no terminal
* javac Main.java && java Main
* ----------------------------------------------
* Para ativar o conector java
* export CLASSPATH=mysql-connector-j-8.4.0.jar:.
* */

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Contato> contatos = new ArrayList<>();

        String url = "jdbc:mysql://acilab.com.br:3309/db2603";
        String user = "root";
        String password = "admin";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexão estabelecida com sucesso!");

            boolean loop = true;

            while (loop) {
                System.out.println("=== MENU AGENDA ===");
                System.out.println("1. Adicionar Contato");
                System.out.println("2. Remover Contato");
                System.out.println("3. Buscar Contato");
                System.out.println("4. Listar Contatos");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 0:
                        loop = false;
                        System.out.println("Encerrando programa.");
                        break;

                    case 1:
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("Telefone: ");
                        String telefone = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();

                        System.out.println("Tipo de contato:");
                        System.out.println("1. Profissional");
                        System.out.println("2. Pessoal");
                        System.out.print("Escolha: ");
                        int tipo = scanner.nextInt();
                        scanner.nextLine();

                        try {
                            PreparedStatement stmt = connection.prepareStatement(
                                "INSERT INTO Contatos (nome, telefone, email) VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                            stmt.setString(1, nome);
                            stmt.setString(2, telefone);
                            stmt.setString(3, email);
                            stmt.executeUpdate();

                            ResultSet rs = stmt.getGeneratedKeys();
                            int contatoId = 0;
                            if (rs.next()) {
                                contatoId = rs.getInt(1);
                            }

                            if (tipo == 1) {
                                System.out.print("Empresa: ");
                                String empresa = scanner.nextLine();
                                System.out.print("Cargo: ");
                                String cargo = scanner.nextLine();
                                contatos.add(new ContatoProfissional(nome, telefone, email, empresa, cargo));

                                PreparedStatement stmtProf = connection.prepareStatement(
                                    "INSERT INTO Contato_profissional (contato_id, empresa, cargo) VALUES (?, ?, ?)");
                                stmtProf.setInt(1, contatoId);
                                stmtProf.setString(2, empresa);
                                stmtProf.setString(3, cargo);
                                stmtProf.executeUpdate();

                            } else if (tipo == 2) {
                                System.out.print("Aniversário(ano-mes-dia): ");
                                String aniversario = scanner.nextLine();
                                System.out.print("Endereço: ");
                                String endereco = scanner.nextLine();
                                contatos.add(new ContatoPessoal(nome, telefone, email, aniversario, endereco));

                                PreparedStatement stmtPes = connection.prepareStatement(
                                    "INSERT INTO Contato_pessoal (contato_id, data_aniversario, endereco) VALUES (?, ?, ?)");
                                stmtPes.setInt(1, contatoId);
                                stmtPes.setDate(2, Date.valueOf(aniversario));
                                stmtPes.setString(3, endereco);
                                stmtPes.executeUpdate();

                            } else {
                                System.out.println("Tipo inválido.");
                            }
                        } catch (Exception e) {
                            System.out.println("Erro ao inserir contato.");
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        System.out.print("Nome do contato para remover: ");
                        String nomeRemover = scanner.nextLine();
                        boolean removido = false;

                        try {
                            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Contatos WHERE nome = ?");
                            stmt.setString(1, nomeRemover);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                int id = rs.getInt("id");

                                PreparedStatement delProf = connection.prepareStatement("DELETE FROM Contato_profissional WHERE contato_id = ?");
                                delProf.setInt(1, id);
                                delProf.executeUpdate();

                                PreparedStatement delPes = connection.prepareStatement("DELETE FROM Contato_pessoal WHERE contato_id = ?");
                                delPes.setInt(1, id);
                                delPes.executeUpdate();

                                PreparedStatement delCont = connection.prepareStatement("DELETE FROM Contatos WHERE id = ?");
                                delCont.setInt(1, id);
                                delCont.executeUpdate();

                                removido = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (removido) {
                            System.out.println("Contato removido com sucesso.");
                        } else {
                            System.out.println("Contato não encontrado.");
                        }
                        break;

                    case 3:
                        System.out.print("Nome do contato para buscar: ");
                        String nomeBuscar = scanner.nextLine();
                        boolean encontrado = false;

                        try {
                            PreparedStatement stmt = connection.prepareStatement(
                                "SELECT c.*, p.empresa, p.cargo, pe.data_aniversario, pe.endereco " +
                                "FROM Contatos c " +
                                "LEFT JOIN Contato_profissional p ON c.id = p.contato_id " +
                                "LEFT JOIN Contato_pessoal pe ON c.id = pe.contato_id " +
                                "WHERE c.nome = ?");
                            stmt.setString(1, nomeBuscar);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                System.out.println("Nome: " + rs.getString("nome"));
                                System.out.println("Telefone: " + rs.getString("telefone"));
                                System.out.println("Email: " + rs.getString("email"));
                                if (rs.getString("empresa") != null) {
                                    System.out.println("Empresa: " + rs.getString("empresa"));
                                    System.out.println("Cargo: " + rs.getString("cargo"));
                                }
                                if (rs.getString("endereco") != null) {
                                    System.out.println("Data de aniversário: " + rs.getString("data_aniversario"));
                                    System.out.println("Endereço: " + rs.getString("endereco"));
                                }
                                encontrado = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!encontrado) {
                            System.out.println("Contato não encontrado.");
                        }
                        break;

                    case 4:
                        try {
                            Statement stmt = connection.createStatement();
                            ResultSet rs = stmt.executeQuery(
                                "SELECT c.*, p.empresa, p.cargo, pe.data_aniversario, pe.endereco " +
                                "FROM Contatos c " +
                                "LEFT JOIN Contato_profissional p ON c.id = p.contato_id " +
                                "LEFT JOIN Contato_pessoal pe ON c.id = pe.contato_id");

                            while (rs.next()) {
                                System.out.println("Nome: " + rs.getString("nome"));
                                System.out.println("Telefone: " + rs.getString("telefone"));
                                System.out.println("Email: " + rs.getString("email"));
                                if (rs.getString("empresa") != null) {
                                    System.out.println("Empresa: " + rs.getString("empresa"));
                                    System.out.println("Cargo: " + rs.getString("cargo"));
                                }
                                if (rs.getString("endereco") != null) {
                                    System.out.println("Data de aniversário: " + rs.getString("data_aniversario"));
                                    System.out.println("Endereço: " + rs.getString("endereco"));
                                }
                                System.out.println("------------------");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        System.out.println("Opção inválida.");
                        break;
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.out.println("Erro na conexão com o banco de dados.");
            e.printStackTrace();
        }
    }
}

