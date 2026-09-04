package com.example.myapplication.db;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.myapplication.model.Todo;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class RoomTodoDAO_Impl implements RoomTodoDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Todo> __insertAdapterOfTodo;

  private final EntityDeleteOrUpdateAdapter<Todo> __deleteAdapterOfTodo;

  private final EntityDeleteOrUpdateAdapter<Todo> __updateAdapterOfTodo;

  public RoomTodoDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTodo = new EntityInsertAdapter<Todo>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `todos` (`id`,`name`,`description`,`done`,`favorite`,`dueDate`,`dueTime`,`linkedContacts`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Todo entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getDescription());
        }
        final int _tmp = entity.isDone() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getDueDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getDueDate());
        }
        if (entity.getDueTime() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getDueTime());
        }
        final String _tmp_2 = RoomConverters.fromArrayListToString(entity.getLinkedContacts());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, _tmp_2);
        }
      }
    };
    this.__deleteAdapterOfTodo = new EntityDeleteOrUpdateAdapter<Todo>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `todos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Todo entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTodo = new EntityDeleteOrUpdateAdapter<Todo>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `todos` SET `id` = ?,`name` = ?,`description` = ?,`done` = ?,`favorite` = ?,`dueDate` = ?,`dueTime` = ?,`linkedContacts` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Todo entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getDescription());
        }
        final int _tmp = entity.isDone() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getDueDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getDueDate());
        }
        if (entity.getDueTime() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getDueTime());
        }
        final String _tmp_2 = RoomConverters.fromArrayListToString(entity.getLinkedContacts());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, _tmp_2);
        }
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public long insert(final Todo todo) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfTodo.insertAndReturnId(_connection, todo);
    });
  }

  @Override
  public void delete(final Todo todo) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfTodo.handle(_connection, todo);
      return null;
    });
  }

  @Override
  public void update(final Todo todo) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfTodo.handle(_connection, todo);
      return null;
    });
  }

  @Override
  public List<Todo> getAllTodos() {
    final String _sql = "SELECT * FROM todos";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfDueTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueTime");
        final int _columnIndexOfLinkedContacts = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "linkedContacts");
        final List<Todo> _result = new ArrayList<Todo>();
        while (_stmt.step()) {
          final Todo _item;
          _item = new Todo();
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          _item.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _item.setDescription(_tmpDescription);
          final boolean _tmpDone;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfDone));
          _tmpDone = _tmp != 0;
          _item.setDone(_tmpDone);
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          _item.setFavorite(_tmpFavorite);
          final String _tmpDueDate;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null;
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate);
          }
          _item.setDueDate(_tmpDueDate);
          final String _tmpDueTime;
          if (_stmt.isNull(_columnIndexOfDueTime)) {
            _tmpDueTime = null;
          } else {
            _tmpDueTime = _stmt.getText(_columnIndexOfDueTime);
          }
          _item.setDueTime(_tmpDueTime);
          final ArrayList<String> _tmpLinkedContacts;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfLinkedContacts)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfLinkedContacts);
          }
          _tmpLinkedContacts = RoomConverters.fromStringToArrayList(_tmp_2);
          _item.setLinkedContacts(_tmpLinkedContacts);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Todo getTodoById(final long id) {
    final String _sql = "SELECT * FROM todos WHERE id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfDueTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueTime");
        final int _columnIndexOfLinkedContacts = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "linkedContacts");
        final Todo _result;
        if (_stmt.step()) {
          _result = new Todo();
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          _result.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result.setName(_tmpName);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _result.setDescription(_tmpDescription);
          final boolean _tmpDone;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfDone));
          _tmpDone = _tmp != 0;
          _result.setDone(_tmpDone);
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          _result.setFavorite(_tmpFavorite);
          final String _tmpDueDate;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null;
          } else {
            _tmpDueDate = _stmt.getText(_columnIndexOfDueDate);
          }
          _result.setDueDate(_tmpDueDate);
          final String _tmpDueTime;
          if (_stmt.isNull(_columnIndexOfDueTime)) {
            _tmpDueTime = null;
          } else {
            _tmpDueTime = _stmt.getText(_columnIndexOfDueTime);
          }
          _result.setDueTime(_tmpDueTime);
          final ArrayList<String> _tmpLinkedContacts;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfLinkedContacts)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfLinkedContacts);
          }
          _tmpLinkedContacts = RoomConverters.fromStringToArrayList(_tmp_2);
          _result.setLinkedContacts(_tmpLinkedContacts);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteAllTodos() {
    final String _sql = "DELETE FROM todos";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
