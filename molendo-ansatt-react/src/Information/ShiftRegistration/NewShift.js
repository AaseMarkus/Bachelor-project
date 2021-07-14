import React, { Component } from 'react'
import NewRegistrationNormal from './../../assets/images/new-registration-normal.PNG'
import NewRegistrationArrow from './../../assets/images/new-registration-arrow.PNG'
import NewRegistrationDropdown from './../../assets/images/new-registration-dropdown.PNG'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class NewShift extends Component {
    render() {
        return (
            <Container>
                <Row>
                    <h2>Registrering av ny vakt</h2>
                </Row>
                <Row>
                    <Col xs={0.1}>
                        <Row><Image src={NewRegistrationNormal} alt="Image of weekbox" width={330} /></Row>
                        <Row><Image src={NewRegistrationArrow} alt="Image of weekbox" width={330} /></Row>
                    </Col>
                    <Col>
                        <Image src={NewRegistrationDropdown} alt="Image of weekbox" width={130} />
                    </Col>
                </Row>
                <Row>
                    For å registrere en ny vakt kan man trykke på Ny knappen på den dagen man ønsker å registrere.
                    Dette vil åpne en boks der man kan velge start tid og slutttid, eller man kan velge at det skal
                    være en pilvakt der man kun velger starttid. Deretter kan man trykke "Velg tid" for å lagre vakten til dagen.
                </Row>
            </Container>
        )
    }
}
