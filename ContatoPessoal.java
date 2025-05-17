public class ContatoPessoal extends Contato {
    String dataAniversario;
    String endereco;

    ContatoPessoal(String nome, String telefone, String email, String dataAniversario, String endereco) {
        super(nome, telefone, email);
        this.dataAniversario = dataAniversario;
        this.endereco = endereco;
    }

    @Override
    void exibirContato() {
        super.exibirContato();
        System.out.println("Data de aniversário: " + dataAniversario);
        System.out.println("Endereço: " + endereco);
    }
}
