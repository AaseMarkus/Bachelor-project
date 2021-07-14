import React, { Component } from 'react'
import DayCopyPopoutImage from './../../assets/images/day-copy-popout.PNG'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class DayCopying extends Component {
    render() {
        return (
            <Container>
                <Row>
                    <h3>Dager</h3>
                </Row>
                <Row>
                    <Image src={DayCopyPopoutImage} alt="Image of weekbox" width={500} />
                </Row>
                <Row>
                    Når man trykker på kopieringsknappen i en dagsboks blir et kopieringsvindu åpnet.
                    Her kan man se alle dagene i lovlig periode inndelt i uker, og man kan markere hvilke dager
                    man ønsker å kopiere til. Når man trykker lagre blir vaktene på valgte dager erstattet med vaktene
                    i den opprinnelige dagen. Dager som allerede har passert kan ikke markeres, og kopiering til dager
                    som er halveis passert vil kun kopiere inn tidspunkter etter nåtid.
                </Row>
            </Container>
        )
    }
}
