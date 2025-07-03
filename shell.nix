# shell.nix
{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    clojure
    openjdk17
    gemini-cli
  ];
  
  shellHook = ''
    export PROJECT_ROOT=$(pwd)
    export DATA_DIR=$PROJECT_ROOT/data
    export MEDIA_DIR=$PROJECT_ROOT/media
    export PATH=$JENA_HOME/bin:$PATH
    
    # Create isolated directories
    mkdir -p $DATA_DIR/{tdb2,temp} $MEDIA_DIR
    
    # Restrict environment to project
    export HOME=$PROJECT_ROOT/.home
    mkdir -p $HOME
    
    # Jena configuration
    export JENA_DB_PATH=$DATA_DIR/tdb2
    export TDB_CONFIG="$DATA_DIR/tdb.properties"
    
    # Create TDB config if it doesn't exist
    if [ ! -f "$TDB_CONFIG" ]; then
      cat > "$TDB_CONFIG" << EOF
# TDB2 Configuration
tdb:DatasetTDB2
tdb:location "$JENA_DB_PATH"
tdb:unionDefaultGraph true
EOF
    fi
    
    echo "Environment ready:"
    echo "  Data store: $JENA_DB_PATH"
    echo "  Media dir:  $MEDIA_DIR"
    echo "  Jena tools: Available in PATH"
  '';

backup-script = pkgs.writeShellScriptBin "backup" ''
  BACKUP_DIR="$PROJECT_ROOT/backups/$(date +%Y%m%d-%H%M%S)"
  mkdir -p "$BACKUP_DIR"
  cp -r "$DATA_DIR" "$MEDIA_DIR" "$BACKUP_DIR/"
  echo "Backup created: $BACKUP_DIR"
'';
}
