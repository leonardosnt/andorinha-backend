package runner;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Essa operação primeiro executa a operação informada no primeiro parâmetro e então
 * atualiza o valor das sequences de acordo com os dados que foram inseridos.
 *
 * Por agora, essa operação assume 2 coisas:
 *  - As tabelas informadas no dataset possuem uma sequence chamada seq_{nome da tabela}
 *  - O campo onde o valor da sequence é usada se chama ID (necessário para buscar o último valor)
 * Isso pode ser alterado caso haja necessidade.
 */
public class UpdateSequenceOperation extends DatabaseOperation {
    private final DatabaseOperation preceding;

    /**
     * @param preceding - Operação que será executada antes de atualizar as sequences
     */
    public UpdateSequenceOperation(DatabaseOperation preceding) {
        this.preceding = preceding;
    }

    @Override
    public void execute(IDatabaseConnection connection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
        if (preceding != null) preceding.execute(connection, dataSet);

        try (Statement st = connection.getConnection().createStatement()) {
            for (String tableName : dataSet.getTableNames()) {
                String tableNameWithoutSchema = tableName.substring(tableName.indexOf('.') + 1);
                String sequenceName = String.format("seq_%s", tableNameWithoutSchema);

                st.addBatch(String.format("SELECT setval('%s', (SELECT max(id) + 1 FROM %s))", sequenceName, tableName));
            }
            st.executeBatch();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
