# shell.nix
{ pkgs ? import <nixpkgs> {} }:

let
  jenaVersion = "4.10.0";
  jenaUrl = "https://archive.apache.org/dist/jena/binaries/apache-jena-${jenaVersion}.tar.gz";
  
  jena = pkgs.stdenv.mkDerivation {
    pname = "apache-jena";
    version = jenaVersion;
    
    src = pkgs.fetchurl {
      url = jenaUrl;
      sha256 = "sha256-PLACEHOLDER"; # Run nix-prefetch-url to get actual hash
    };
    
    installPhase = ''
      mkdir -p $out
      cp -r * $out/
      chmod +x $out/bin/*
    '';
  };

in pkgs.mkShell {
  buildInputs = with pkgs; [
    clojure
    openjdk17
    jena
  ];
  
  shellHook = ''
    export PROJECT_ROOT=$(pwd)
    export DATA_DIR=$PROJECT_ROOT/data
    export MEDIA_DIR=$PROJECT_ROOT/media
    export JENA_HOME=${jena}
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
}
