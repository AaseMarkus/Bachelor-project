import React from 'react'
import { Form, Button, Container, Row, Col } from 'react-bootstrap'
import i18n from './../Language/i18n'

export default function LoginPhone(props) {
    return (
        <div>
            <Form>
                <Form.Group>
                    <Form.Control
                        aria-label="input phone number"
                        type="phone"
                        name="phone"
                        value={props.phoneNumber}
                        placeholder={i18n.t('login.input-phone')}
                        autoFocus
                        onChange={(event) => props.handleChange(event)} 
                    />
                </Form.Group>
                <Container>
                    <Row noGutters="true" className="justify-content-center">
                        <Col xs="auto" sm="auto">
                            <Button
                                type="submit"
                                name="sendSMS"
                                onClick={(event) => props.handleNextClick(event)}>
                                    {i18n.t('login.send-sms')}
                            </Button>
                        </Col>
                    </Row>
                </Container>
                
            </Form>
        </div>
    )
}
