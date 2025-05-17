public class ContatoProfissional extends Contato {
    String empresa;
    String cargo;

    ContatoProfissional(String nome, String telefone, String email, String empresa, String cargo) {
        super(nome, telefone, email);
        this.empresa = empresa;
        this.cargo = cargo;
    }

    @Override
    void exibirContato() {
        super.exibirContato();
        System.out.println("Empresa: " + empresa);
        System.out.println("Cargo: " + cargo);
    }
}
