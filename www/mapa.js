var Mapa = React.createClass({
    getInitialState: function () {
        return {
            cidade: {},
            objetos: []
        };
    },
    atualiza: function () {
        var self = this;

        var r = new XMLHttpRequest();
        r.open("get", "mapa", true);
        r.onreadystatechange = function () {
            if (r.readyState != 4) return;

            if (r.status == 200) {
                var linhas = r.responseText.trim().split('\n');
                var cidade = linhas.shift().split(',');

                self.setState({
                    error: false,
                    cidade: {
                        nome: cidade[0],
                        tamanhoLat: cidade[1],
                        tamanhoLong: cidade[2]
                    },
                    objetos: linhas.map(function (l) {
                        var o = l.split(',');
                        return {
                            descricao: o[0],
                            tipo: o[1],
                            lat: o[2],
                            lng: o[3]
                        };
                    })
                });
            } else
                self.setState({
                    error: true
                });

            setTimeout(self.atualiza, 33);

        };
        r.send("");
    },
    componentDidMount: function () {
        setTimeout(this.atualiza, 33);
    },
    render: function () {
        return React.createElement('div', {}, this.state.cidade.nome,
                                   (this.state.error?'  (ERRO DE COMUNICACAO COM O SERVIDOR)': ''),
            React.createElement('div', {
                    className: 'mapa',
                    style: {
                        width: this.state.cidade.tamanhoLat,
                        height: this.state.cidade.tamanhoLong
                    }
                },
                this.state.objetos.map(function (o) {
                    return React.createElement('div', {
                        className: o.tipo,
                        title: o.descricao,
                        style: {
                            left: o.lat,
                            top: o.lng
                        }
                    });
                })
            )
        );
    }
});

React.render(React.createElement(Mapa), document.getElementById('mapa'));
